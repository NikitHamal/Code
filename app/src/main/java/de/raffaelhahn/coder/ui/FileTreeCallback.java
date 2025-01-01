package de.raffaelhahn.coder.ui;

public interface FileTreeCallback {
    void onFileSelected(String path);
    void onFileDeleted(String path);
    void onFileRenamed(String path, String newName);
    void onFileCreated(String path);
}
