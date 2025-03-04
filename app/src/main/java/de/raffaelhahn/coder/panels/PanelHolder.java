package de.raffaelhahn.coder.panels;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.raffaelhahn.coder.R;

public class PanelHolder extends Fragment implements PanelIconsAdapter.PanelSelectionCallback {

    private RecyclerView iconsRecyclerView;
    private PanelIconsAdapter panelIconsAdapter;
    private Panel postponedShowPanel;
    private int placeholderId;


    public PanelHolder() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        panelIconsAdapter = new PanelIconsAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panel_holder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconsRecyclerView = view.findViewById(R.id.panelIconsRecycler);
        iconsRecyclerView.setAdapter(panelIconsAdapter);
        iconsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        View panelFragmentPlaceholder = view.findViewById(R.id.panelFragmentPlaceholder);
        placeholderId = View.generateViewId();
        panelFragmentPlaceholder.setId(placeholderId);

        if(postponedShowPanel != null) {
            showPanelInternal(postponedShowPanel);
            postponedShowPanel = null;
        }
    }

    @Override
    public void onPanelSelected(Panel panel) {
        showPanel(panel);
    }

    /**
     * Show a panels fragment in the panel holder.
     * If the panel is not already in the list of panels, it will be added.
     * If the layout is not yet inflated, the panel will be shown after the layout is inflated.
     * @param panel The panel to show
     */
    public void showPanel(Panel panel) {
        if(!panelIconsAdapter.getPanels().contains(panel)) {
            addPanel(panel);
        }
        if(getView() != null) {
            showPanelInternal(panel);
        } else {
            postponedShowPanel = panel;
        }
    }

    private void showPanelInternal(Panel panel) {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(placeholderId, panel.getFragment());
        ft.commit();
    }

    /**
     * Add a panel to the list of panels.
     * This doesn't immediately show the panels fragment in the panel holder.
     * @param panel The panel to add
     */
    public void addPanel(Panel panel) {
        panelIconsAdapter.getPanels().add(panel);
        panelIconsAdapter.notifyItemInserted(panelIconsAdapter.getPanels().size() - 1);
    }

    /**
     * Remove a panel from the list of panels.
     * @param panel The panel to remove
     */
    public void removePanel(Panel panel) {
        int index = panelIconsAdapter.getPanels().indexOf(panel);
        panelIconsAdapter.getPanels().remove(panel);
        panelIconsAdapter.notifyItemRemoved(index);
    }
}