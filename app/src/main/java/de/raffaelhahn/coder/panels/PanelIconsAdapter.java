package de.raffaelhahn.coder.panels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.projectmanagement.Project;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class PanelIconsAdapter extends RecyclerView.Adapter<PanelIconsAdapter.ViewHolder> {

    @Getter
    @Setter
    private List<Panel> panels = new ArrayList<>();

    private final PanelSelectionCallback callback;

    @NonNull
    @Override
    public PanelIconsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.panel_icon_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PanelIconsAdapter.ViewHolder holder, int position) {
        Panel panel = panels.get(position);
        holder.icon.setImageResource(panel.getIcon());

        holder.itemView.setOnClickListener(v -> callback.onPanelSelected(panel));
    }

    @Override
    public int getItemCount() {
        return panels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.panelIcon);
        }
    }

    public interface PanelSelectionCallback {
        void onPanelSelected(Panel panel);
    }
}
