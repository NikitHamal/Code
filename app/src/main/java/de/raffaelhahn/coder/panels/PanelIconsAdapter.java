package de.raffaelhahn.coder.panels;

import android.content.ClipData;
import android.content.ClipDescription;
import android.util.Pair;
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

    private final PanelHolder panelHolder;
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

        holder.itemView.setTag("panel");

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());

                ClipData dragData = new ClipData(
                        (CharSequence) v.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(holder.itemView);

                v.startDragAndDrop(dragData,  // The data to be dragged.
                        myShadow,  // The drag shadow builder.
                        new Pair<>(panelHolder, panel),      // No need to use local data.
                        0          // Flags. Not currently used, set to 0.
                );

                // Indicate that the long-click is handled.
                return true;
            }
        });
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
