package de.raffaelhahn.coder.files;

import android.content.Context;
import android.os.FileObserver;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import de.raffaelhahn.coder.R;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileManager {

    private final String path;
    private final ArrayList<FileChangeListener> listeners = new ArrayList<>();
    private RecursiveFileObserver observer;

    public void startObserving() {
        observer = new RecursiveFileObserver(path){
            @Override
            public void onEvent(int event, String path) {
                switch (event) {
                    case FileObserver.CREATE:
                        listeners.stream().collect(Collectors.toList()).forEach(listener -> listener.onFileCreate(path));
                        break;
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
                Files.createDirectories(pathObj.getParent());
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
