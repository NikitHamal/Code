package de.raffaelhahn.coder.filetree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class FileTreeNode {

    private FileTreeNode parentNode;
    private File file;
    private boolean showChildren = false;
    private ArrayList<FileTreeNode> children = new ArrayList<>();

    public FileTreeNode(File file) {
        this(null, file);
    }
    public FileTreeNode(FileTreeNode parentNode, File file) {
        this.parentNode = parentNode;
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

    public void refreshChildren() {
        if(showChildren) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if(children.stream().noneMatch(exChild -> exChild.getFile().equals(f))) {
                        children.add(new FileTreeNode(f));
                    }
                }
                for (FileTreeNode child : children) {
                    if (!child.getFile().exists()) {
                        children.remove(child);
                    }
                }
            }
        }
    }

    public FileTreeNode findNode(String path) {
        if(file.getPath().equals(path)) {
            return this;
        } else {
            for(FileTreeNode child : children) {
                FileTreeNode result = child.findNode(path);
                if(result != null) {
                    return result;
                }
            }
            return null;
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
