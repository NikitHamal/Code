package de.raffaelhahn.coder.filetree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class FileTreeNode {

    private File file;
    private boolean showChildren = false;
    private ArrayList<FileTreeNode> children = new ArrayList<>();

    public FileTreeNode(File file) {
        this.file = file;
    }

    public void setShowChildren(boolean showChildren) {
        if(isDirectory()) {
            this.showChildren = showChildren;
            if (showChildren) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        children.add(new FileTreeNode(f));
                    }
                }
            } else {
                children.clear();
            }
        }
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public List<FileTreeNode> listFilesRecursively() {
        ArrayList<FileTreeNode> result = new ArrayList<>();
        result.add(this);
        for(FileTreeNode child : children) {
            result.addAll(child.listFilesRecursively());
        }
        return result;
    }

}
