package de.raffaelhahn.coder.projectmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.filetree.FileTreeCallback;
import de.raffaelhahn.coder.filetree.FileTreeNode;
import de.raffaelhahn.coder.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    @Getter
    @Setter
    private List<Project> projects = new ArrayList<>();

    private final ProjectAdapterCallback callback;

    @NonNull
    @Override
    public ProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.projectName.setText(project.getPath());

        holder.itemView.setOnClickListener(v -> callback.onProjectSelected(project));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView projectName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.projectItemName);
        }
    }

    public interface ProjectAdapterCallback {
        void onProjectSelected(Project project);
    }
}
