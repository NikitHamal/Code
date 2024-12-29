package de.raffaelhahn.coder.ui.recyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.ui.FileTreeFragment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

public class FileTreeAdapter extends RecyclerView.Adapter<FileTreeAdapter.ViewHolder> {

    @Setter
    private List<FileTreeItem> files = new ArrayList<>();

    @NonNull
    @Override
    public FileTreeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_tree_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FileTreeAdapter.ViewHolder holder, int position) {
        FileTreeItem file = files.get(position);
        if(file.isDirectory()) {
            holder.fileIcon.setImageResource(R.drawable.folder);
            holder.fileChevron.setVisibility(View.VISIBLE);
            if(file.isUnfolded()) {
                holder.fileChevron.setImageResource(R.drawable.chevron_down);
            } else {
                holder.fileChevron.setImageResource(R.drawable.chevron_right);
            }
        } else {
            holder.fileIcon.setImageResource(R.drawable.file);
            holder.fileChevron.setVisibility(View.INVISIBLE);
        }
        holder.fileName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fileName;
        public ImageView fileChevron;
        public ImageView fileIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileTreeItemName);
            fileChevron = itemView.findViewById(R.id.fileTreeItemChevron);
            fileIcon = itemView.findViewById(R.id.fileTreeItemIcon);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FileTreeItem {
        private String filePath;
        private String name;
        private boolean directory;
        private boolean unfolded;
        private boolean shown;
        private int depth;
    }
}
