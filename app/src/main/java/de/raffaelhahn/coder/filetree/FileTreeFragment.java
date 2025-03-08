package de.raffaelhahn.coder.filetree;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.MainActivity;
import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.files.FileCreationDialog;
import de.raffaelhahn.coder.files.FileManager;
import de.raffaelhahn.coder.projectmanagement.Project;

public class FileTreeFragment extends Fragment implements FileTreeCallback, FileManager.FileChangeListener {

    private RecyclerView recyclerView;
    private FileTreeAdapter adapter;
    private FileTreeNode rootNode;
    private ArrayList<String> openedDirectories = new ArrayList<>();

    public FileTreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new FileTreeAdapter(this, this);
        String rootPath = ((MainActivity) requireActivity()).getPath();
        if (rootPath == null) {
            return;
        }
        setRootFile(new File(rootPath));
        ((MainActivity) getActivity()).getFileManager().addFileListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).getFileManager().removeFileListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_file_tree, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.fileTreeRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateTree();
    }

    private void showFileCreationDialog(String path, boolean directory) {
        FileCreationDialog
                .newInstance(path, directory)
                .show(getParentFragmentManager(), "file_creation_dialog");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getTag() instanceof FileTreeNode fileNode) {
            if (fileNode.isDirectory()) {
                SubMenu subMenuNew = menu.addSubMenu(R.string.new_submenu).setIcon(R.drawable.add);
                subMenuNew.add(R.string.create_new_file).setIcon(R.drawable.file)
                        .setOnMenuItemClickListener(item -> {
                            showFileCreationDialog(fileNode.getFile().getAbsolutePath(), false);
                            return true;
                        });
                subMenuNew.add(R.string.create_new_directory).setIcon(R.drawable.folder)
                        .setOnMenuItemClickListener(item -> {
                            showFileCreationDialog(fileNode.getFile().getAbsolutePath(), true);
                            return true;
                        });
            }
            if(!fileNode.equals(rootNode)) {
                menu.add(R.string.rename);
                menu.add(R.string.delete).setOnMenuItemClickListener(item -> {
                    FileManager.deleteFile(fileNode.getFile().getAbsolutePath());
                    return true;
                });
            }
        }
    }

    private void setRootFile(File file) {
        rootNode = new FileTreeNode(file);
        rootNode.setShowChildren(true);
        adapter.setFiles(rootNode);
    }

    public void updateTree() {
        requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onFileTreeNodeSelected(FileTreeNode fileTreeNode) {
        ((FileTreeCallback) getActivity()).onFileTreeNodeSelected(fileTreeNode);
        if (fileTreeNode.isDirectory()) {
            fileTreeNode.setShowChildren(!fileTreeNode.isShowChildren());
            adapter.setFiles(rootNode);
            adapter.notifyDataSetChanged();
        }
    }

    private void refreshParent(String path) {
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        FileTreeNode parentNode = rootNode.findNode(parentPath);
        if (parentNode != null) {
            parentNode.refreshChildren();
            adapter.setFiles(rootNode);
            updateTree();
        }
    }

    @Override
    public void onFileCreate(String path) {
        refreshParent(path);
    }

    @Override
    public void onFileDelete(String path) {
        refreshParent(path);
    }

    @Override
    public void onFileModify(String path) {
        refreshParent(path);
    }

    @Override
    public void onFileMovedFrom(String path) {
        refreshParent(path);
    }

    @Override
    public void onFileMovedTo(String path) {
        refreshParent(path);
    }

    @Override
    public void onFileAttributeChange(String path) {
        refreshParent(path);
    }

    @Override
    public void onOtherEvent(String path) {
        // Do nothing
    }
}