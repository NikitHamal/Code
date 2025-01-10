package de.raffaelhahn.coder.terminal;

import static de.raffaelhahn.coder.terminal.termux.TermuxConstants.TERMUX_PREFIX_DIR;
import static de.raffaelhahn.coder.terminal.termux.TermuxConstants.TERMUX_PREFIX_DIR_PATH;
import static de.raffaelhahn.coder.terminal.termux.TermuxConstants.TERMUX_STAGING_PREFIX_DIR;
import static de.raffaelhahn.coder.terminal.termux.TermuxConstants.TERMUX_STAGING_PREFIX_DIR_PATH;
import static de.raffaelhahn.coder.terminal.TermuxInstaller.loadZipBytes;

import android.content.Context;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.raffaelhahn.coder.terminal.termux.FileUtils;
import de.raffaelhahn.coder.terminal.termux.TermuxConstants;
import de.raffaelhahn.coder.terminal.termux.TermuxFileUtils;

import de.raffaelhahn.coder.terminal.termux.Error;

public class TerminalInstaller {

    public TerminalInstaller() {

    }

    public void install() {

        new Thread(() -> {
            try {
                // Delete prefix staging directory or any file at its destination
                File stagingPrefixFile = new File(TERMUX_STAGING_PREFIX_DIR_PATH);
                if (stagingPrefixFile.exists() && !deleteDir(stagingPrefixFile)) {
                    //showBootstrapErrorDialog(activity, whenDone, "Unable to delete old staging area.");
                    return;
                }

                File prefixFile = new File(TERMUX_STAGING_PREFIX_DIR_PATH);
                if (prefixFile.exists() && !deleteDir(prefixFile)) {
                    //showBootstrapErrorDialog(activity, whenDone, "Unable to delete old PREFIX.");
                    return;
                }

                final byte[] buffer = new byte[8096];
                final List<Pair<String, String>> symlinks = new ArrayList<>(50);

                final byte[] zipBytes = loadZipBytes();
                try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
                    ZipEntry zipEntry;
                    while ((zipEntry = zipInput.getNextEntry()) != null) {
                        Log.i("TerminalInstaller", "Extracting " + zipEntry.getName());
                        if (zipEntry.getName().equals("SYMLINKS.txt")) {
                            BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                            String line;
                            while ((line = symlinksReader.readLine()) != null) {
                                String[] parts = line.split("←");
                                if (parts.length != 2)
                                    throw new RuntimeException("Malformed symlink line: " + line);
                                String oldPath = parts[0];
                                String newPath = TERMUX_STAGING_PREFIX_DIR_PATH + "/" + parts[1];
                                symlinks.add(Pair.create(oldPath, newPath));
                            }
                        } else {
                            String zipEntryName = zipEntry.getName();
                            File targetFile = new File(TERMUX_STAGING_PREFIX_DIR_PATH, zipEntryName);

                            // Silence google play scanning flagging about this: https://support.google.com/faqs/answer/9294009
                            var canonicalPath = targetFile.getCanonicalPath();
                            if (!canonicalPath.startsWith(TERMUX_STAGING_PREFIX_DIR_PATH)) {
                                throw new RuntimeException("Invalid zip entry: " + zipEntryName);
                            }

                            boolean isDirectory = zipEntry.isDirectory();

                            if (isDirectory) {
                                targetFile.mkdirs();
                            } else {
                                File parentDir = targetFile.getParentFile();
                                if (!parentDir.exists() && !parentDir.mkdirs()) {
                                    throw new RuntimeException("Cannot create parent dir for: " + targetFile.getAbsolutePath());
                                }
                                try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                                    int readBytes;
                                    while ((readBytes = zipInput.read(buffer)) != -1)
                                        outStream.write(buffer, 0, readBytes);
                                }
                                if (zipEntryName.startsWith("bin/") || zipEntryName.startsWith("libexec") ||
                                        zipEntryName.startsWith("lib/apt/apt-helper") || zipEntryName.startsWith("lib/apt/methods")) {
                                    //noinspection OctalInteger
                                    Os.chmod(targetFile.getAbsolutePath(), 0700);
                                }
                            }
                        }
                    }
                }

                for (Pair<String, String> symlink : symlinks) {
                    var linkFile = new File(symlink.second);
                    if (!linkFile.getParentFile().exists() && !linkFile.getParentFile().mkdirs()) {
                        throw new RuntimeException("Cannot create dir: " + linkFile.getParentFile());
                    }
                    Os.symlink(symlink.first, symlink.second);
                }

                Os.rename(TERMUX_STAGING_PREFIX_DIR_PATH, TermuxConstants.PREFIX_PATH);
                Log.i("TerminalInstaller", "Installation done");

                //activity.runOnUiThread(whenDone);
            } catch (final Exception e) {
                Log.e("TerminalInstaller", "Error in installation", e);
                //showBootstrapErrorDialog(activity, whenDone, "Error in installation: " + e.getMessage());
            } finally {
                /*activity.runOnUiThread(() -> {
                    try {
                        progress.dismiss();
                    } catch (RuntimeException e) {
                        // Activity already dismissed - ignore.
                    }
                });*/
            }
        }).start();

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }


    public static void setupAppLibSymlink(Context context) {
        var nativeLibraryDir = context.getApplicationInfo().nativeLibraryDir;
        var targetFile = new File(TermuxConstants.APP_LIB_PATH);
        new Thread(() -> {
            try {
                if (targetFile.exists()) {
                    if (targetFile.getCanonicalPath().equals(nativeLibraryDir)) {
                        return;
                    } else {
                        Log.w("TerminalInstaller", "Existing incorrect symlink: " + targetFile.getAbsolutePath());
                        if (!targetFile.delete()) {
                            Log.e("TerminalInstaller", "Cannot delete: " + targetFile.getAbsolutePath());
                            return;
                        }
                    }
                } else {
                    if (Files.isSymbolicLink(targetFile.toPath())) {
                        Log.w("TerminalInstaller", "Broken symlink - deleting: " + targetFile.getAbsolutePath());
                        if (!targetFile.delete()) {
                            Log.e("TerminalInstaller", "Could not delete broken symlink: " + targetFile.getAbsolutePath());
                            return;
                        }
                    }
                }
                // Make sures the files dir exists.
                context.getFilesDir();
                Os.symlink(nativeLibraryDir, targetFile.getAbsolutePath());
            } catch (ErrnoException | IOException e) {
                Log.e("TerminalInstaller", "Error symlinking " + nativeLibraryDir + " <- " + targetFile.getAbsolutePath(), e);
            }
        }).start();
    }

    private static Error ensureDirectoryExists(File directory) {
        return FileUtils.createDirectoryFile(directory.getAbsolutePath());
    }
}
