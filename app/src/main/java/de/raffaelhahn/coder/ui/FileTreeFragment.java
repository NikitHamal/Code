package de.raffaelhahn.coder.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.ui.adapters.FileTreeAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileTreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileTreeFragment extends Fragment implements FileTreeCallback {

    private static final String ARG_PATH = "paramPath";

    private String rootPath;

    private RecyclerView recyclerView;
    private FileTreeAdapter adapter;

    public FileTreeFragment() {
        // Required empty public constructor
    }

    public static FileTreeFragment newInstance(String path) {
        FileTreeFragment fragment = new FileTreeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rootPath = getArguments().getString(ARG_PATH);
        }
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
        adapter = new FileTreeAdapter(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        registerForContextMenu(recyclerView);

        setRootFile(new File(rootPath));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.file_tree_context_menu, menu);
    }

    private void setRootFile(File file) {
        try {
            List<Path> filePaths = Files
                    .find(file.toPath(), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile() || fileAttr.isDirectory())
                    .collect(Collectors.toList());

            ArrayList<FileTreeAdapter.FileTreeItem> fileTreeItems = new ArrayList<>();
            int rootNameCount = file.toPath().getNameCount();
            for(Path path : filePaths) {
                FileTreeAdapter.FileTreeItem item = FileTreeAdapter.FileTreeItem.builder()
                        .filePath(path.toString())
                        .name(path.getFileName().toString())
                        .directory(Files.isDirectory(path))
                        .depth(path.getNameCount() - rootNameCount)
                        .unfolded(true)
                        .shown(true)
                        .build();

                fileTreeItems.add(item);
            }

            adapter.setFiles(fileTreeItems);
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onFileSelected(String path) {
        ((FileTreeCallback) getActivity()).onFileSelected(path);
        File file = new File(path);
        if(file.isDirectory()) {
            List<FileTreeAdapter.FileTreeItem> files = adapter.getFiles();
            FileTreeAdapter.FileTreeItem fileTreeItem = files.stream().filter(f -> f.getFilePath().equals(path)).findFirst().get();
            files.stream()
                    .filter(f -> f.getFilePath().startsWith(path) && !f.getFilePath().equals(path))
                    .forEach(f -> {
                        if(fileTreeItem.getDepth() + 1 == f.getDepth()) {
                            f.setShown(!fileTreeItem.isUnfolded());
                        } else if(fileTreeItem.getDepth() + 1 > f.getDepth()) {
                            f.setShown(false);
                        }
                        if(fileTreeItem.isUnfolded()) {
                            f.setShown(false);
                        }
                        f.setUnfolded(false);
                    });
            fileTreeItem.setUnfolded(!fileTreeItem.isUnfolded());
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onFileDeleted(String path) {
        ((FileTreeCallback) getActivity()).onFileDeleted(path);
    }

    @Override
    public void onFileRenamed(String path, String newName) {
        ((FileTreeCallback) getActivity()).onFileRenamed(path, newName);
    }

    @Override
    public void onFileCreated(String path) {
        ((FileTreeCallback) getActivity()).onFileCreated(path);
    }

}