package de.raffaelhahn.coder.files;

import android.content.Context;
import android.os.FileObserver;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import de.raffaelhahn.coder.R;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileManager {

    /**
     * Event type: A new subdirectory was created under the monitored directory<br>
     * For some reason this constant is undocumented in {@link FileObserver}.
     */
    private static final int CREATE_DIR = 0x40000100;
    /**
     * Event type: A subdirectory was deleted from the monitored directory<br>
     * For some reason this constant is undocumented in {@link FileObserver}.
     */
    private static final int DELETE_DIR = 0x40000200;

    private final String path;
    private final ArrayList<FileChangeListener> listeners = new ArrayList<>();
    private RecursiveFileObserver observer;

    public void startObserving() {
        observer = new RecursiveFileObserver(path){
            @Override
            public void onEvent(int event, String path) {
                switch (event) {
                    case CREATE_DIR:
                    case FileObserver.CREATE:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileCreate(path));
                        stopWatching();
                        startWatching();
                        break;
                    case DELETE_DIR:
                    case FileObserver.DELETE:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileDelete(path));
                        break;
                    case FileObserver.MODIFY:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileModify(path));
                        break;
                    case FileObserver.MOVED_FROM:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileMovedFrom(path));
                        break;
                    case FileObserver.MOVED_TO:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileMovedTo(path));
                        break;
                    case FileObserver.ATTRIB:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileAttributeChange(path));
                        break;
                    default:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onOtherEvent(path));
                        break;
                }
            }
        };
        observer.startWatching();
    }

    public void stopObserving() {
        observer.stopWatching();
    }

    public void addFileListener(FileChangeListener listener) {
        listeners.add(listener);
    }

    public void removeFileListener(FileChangeListener listener) {
        listeners.remove(listener);
    }

    public void removeAllFileListeners() {
        listeners.clear();
    }







    /**
     * Create a file or directory
     *
     * @param path         The path to the file or directory
     * @param isDirectory  If the path is a directory
     * @return Null if the file or directory was created, String with the error message if not
     */
    public static String createFile(Context context, String path, boolean isDirectory) {
        Path pathObj = Paths.get(path);
        try {
            if (isDirectory) {
                Files.createDirectories(pathObj);
            } else {
                Files.createDirectories(pathObj.getParent());
                Files.createFile(pathObj);
            }
        } catch(FileAlreadyExistsException e) {
            return context.getString(R.string.file_already_exists);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
        }
        return null;
    }

    public static String deleteFile(String path) {
        Path pathObj = Paths.get(path);
        try {
            if (Files.isDirectory(pathObj)) {
                FileUtils.deleteDirectory(pathObj.toFile());
            } else {
                Files.delete(pathObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getClass().getSimpleName() + ": " + e.getLocalizedMessage();
        }
        return null;
    }

    public interface FileChangeListener {
        void onFileCreate(String path);
        void onFileDelete(String path);
        void onFileModify(String path);
        void onFileMovedFrom(String path);
        void onFileMovedTo(String path);
        void onFileAttributeChange(String path);
        void onOtherEvent(String path);
    }

}
