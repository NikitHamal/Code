package de.raffaelhahn.coder.filetree;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.projectmanagement.Project;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileTreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileTreeFragment extends Fragment implements FileTreeCallback {

    private RecyclerView recyclerView;
    private FileTreeAdapter adapter;
    private FileTreeNode rootNode;

    public FileTreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        Project currentProject = ((CoderApp) getActivity().getApplication()).getFileAppContext().getCurrentProject();
        if(currentProject == null) {
            return;
        }
        String rootPath = currentProject.getPath();

        setRootFile(new File(rootPath));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        SubMenu subMenuNew = menu.addSubMenu(R.string.new_submenu).setIcon(R.drawable.add);
        subMenuNew.add(R.string.create_new_file).setIcon(R.drawable.file);
        subMenuNew.add(R.string.create_new_directory).setIcon(R.drawable.folder);

        menu.add(R.string.rename);
        menu.add(R.string.delete);
        /*MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.file_tree_context_menu, menu);*/
    }

    private void setRootFile(File file) {
        rootNode = new FileTreeNode(file);
        adapter.setFiles(rootNode);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFileTreeNodeSelected(FileTreeNode fileTreeNode) {
        ((FileTreeCallback) getActivity()).onFileTreeNodeSelected(fileTreeNode);
        if(fileTreeNode.isDirectory()) {
            fileTreeNode.setShowChildren(!fileTreeNode.isShowChildren());
            adapter.setFiles(rootNode);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFileTreeNodeDeleteTriggered(FileTreeNode fileTreeNode) {
        ((FileTreeCallback) getActivity()).onFileTreeNodeDeleteTriggered(fileTreeNode);
    }

    @Override
    public void onFileTreeNodeRenameTriggered(FileTreeNode fileTreeNode, String newName) {
        ((FileTreeCallback) getActivity()).onFileTreeNodeRenameTriggered(fileTreeNode, newName);
    }

    @Override
    public void onFileTreeNodeCreateTriggered(FileTreeNode parentFileTreeNode, String fileName) {
        ((FileTreeCallback) getActivity()).onFileTreeNodeCreateTriggered(parentFileTreeNode, fileName);
    }


}