package de.raffaelhahn.coder.filetree;

public interface FileTreeCallback {
    void onFileTreeNodeSelected(FileTreeNode fileTreeNode);
    void onFileTreeNodeDeleteTriggered(FileTreeNode fileTreeNode);
    void onFileTreeNodeRenameTriggered(FileTreeNode fileTreeNode, String newName);
    void onFileTreeNodeCreateTriggered(FileTreeNode parentFileTreeNode, String fileName);
}
