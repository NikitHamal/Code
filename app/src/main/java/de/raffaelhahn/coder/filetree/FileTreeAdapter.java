package de.raffaelhahn.coder.filetree;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.utils.Utils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileTreeAdapter extends RecyclerView.Adapter<FileTreeAdapter.ViewHolder> {

    private List<FileTreeNode> files = new ArrayList<>();
    private int rootNameCount;

    private final Fragment fragment;
    private final FileTreeCallback callback;

    @NonNull
    @Override
    public FileTreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_tree_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileTreeAdapter.ViewHolder holder, int position) {
        FileTreeNode fileNode = files.get(position);
        if(fileNode.isDirectory()) {
            holder.fileIcon.setImageResource(R.drawable.folder);
            holder.fileChevron.setVisibility(View.VISIBLE);
            if(fileNode.isShowChildren()) {
                holder.fileChevron.setImageResource(R.drawable.chevron_down);
            } else {
                holder.fileChevron.setImageResource(R.drawable.chevron_right);
            }
        } else {
            holder.fileIcon.setImageResource(R.drawable.file);
            holder.fileChevron.setVisibility(View.INVISIBLE);
        }

        holder.spacing.getLayoutParams().width = (fileNode.getFile().toPath().getNameCount() - rootNameCount) * Utils.dpToPx(holder.itemView.getContext(), 16);
        holder.fileName.setText(fileNode.getFile().getName());

        holder.itemView.setOnClickListener(v -> callback.onFileTreeNodeSelected(fileNode));

        holder.itemView.setTag(fileNode);
        fragment.registerForContextMenu(holder.itemView);
    }

    public void setFiles(FileTreeNode rootNode) {
        this.rootNameCount = rootNode.getFile().toPath().getNameCount();
        this.files.clear();
        this.files.addAll(rootNode.listFilesRecursively());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View spacing;
        public TextView fileName;
        public ImageView fileChevron;
        public ImageView fileIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            spacing = itemView.findViewById(R.id.fileTreeItemSpacing);
            fileName = itemView.findViewById(R.id.fileTreeItemName);
            fileChevron = itemView.findViewById(R.id.fileTreeItemChevron);
            fileIcon = itemView.findViewById(R.id.fileTreeItemIcon);
        }
    }
}
