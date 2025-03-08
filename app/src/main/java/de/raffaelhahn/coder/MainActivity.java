package de.raffaelhahn.coder;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.raffaelhahn.coder.files.FileManager;
import de.raffaelhahn.coder.filetree.FileTreeCallback;
import de.raffaelhahn.coder.filetree.FileTreeFragment;
import de.raffaelhahn.coder.filetree.FileTreeNode;
import de.raffaelhahn.coder.editor.CodeEditorPagerAdapter;
import de.raffaelhahn.coder.panels.Panel;
import de.raffaelhahn.coder.panels.PanelHolder;
import de.raffaelhahn.coder.terminal.Terminal;
import de.raffaelhahn.coder.terminal.TerminalFragment;
import lombok.Getter;

public class MainActivity extends AppCompatActivity implements FileTreeCallback {

    private FragmentContainerView leftPanelHolderContainer;
    private PanelHolder leftPanelHolder;
    private FragmentContainerView bottomPanelHolderContainer;
    private PanelHolder bottomPanelHolder;
    private ViewPager2 codeEditorViewPager;
    private CodeEditorPagerAdapter codeEditorPagerAdapter;
    private TabLayout editorTabs;
    private ViewSwitcher codeEditorSwitcher;

    @Getter
    private String path;

    @Getter
    private Terminal terminal;
    @Getter
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        terminal = new Terminal();

        Bundle b = getIntent().getExtras();
        path = b.getString("path");
        fileManager = new FileManager(path);
        fileManager.startObserving();

        codeEditorPagerAdapter = new CodeEditorPagerAdapter(this);

        leftPanelHolderContainer = findViewById(R.id.leftPanelHolder);
        leftPanelHolder = leftPanelHolderContainer.getFragment();
        bottomPanelHolderContainer = findViewById(R.id.bottomPanelHolder);
        bottomPanelHolder = bottomPanelHolderContainer.getFragment();
        codeEditorViewPager = findViewById(R.id.codeEditorViewPager);
        codeEditorSwitcher = findViewById(R.id.codeEditorSwitcher);
        editorTabs = findViewById(R.id.codeEditorTabs);
        codeEditorViewPager.setAdapter(codeEditorPagerAdapter);
        codeEditorViewPager.setUserInputEnabled(false);

        new TabLayoutMediator(editorTabs, codeEditorViewPager, (tab, position) -> {
            tab.setCustomView(R.layout.editor_tab_item);
            TextView titleView = tab.getCustomView().findViewById(R.id.editorTabText);
            titleView.setText(codeEditorPagerAdapter.getPaths().get(position));
            tab.setText(codeEditorPagerAdapter.getPaths().get(position));
            tab.getCustomView().findViewById(R.id.editorTabCloseButton).setOnClickListener(v -> {
                codeEditorPagerAdapter.getPaths().remove(position);
                codeEditorPagerAdapter.notifyItemRemoved(position);
            });
            tab.getCustomView().setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN &&
                        (event.getButtonState() & MotionEvent.BUTTON_TERTIARY) != 0) {
                    codeEditorPagerAdapter.getPaths().remove(position);
                    codeEditorPagerAdapter.notifyItemRemoved(position);
                    v.performClick();
                    return true;
                }
                return false;
            });
        }).attach();
        makeDraggable(findViewById(R.id.dividerLeftPanel), leftPanelHolderContainer, findViewById(R.id.codeEditorSwitcher), true);
        makeDraggable(findViewById(R.id.dividerBottomPanel), (View) codeEditorSwitcher.getParent(), bottomPanelHolderContainer, false);

        codeEditorPagerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                showOrHideNoFileSelectedMessage();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                showOrHideNoFileSelectedMessage();
            }
        });


        showOrHideNoFileSelectedMessage();
        leftPanelHolder.showPanel(new Panel(R.drawable.file_tree, "File Tree", FileTreeFragment.class));
        bottomPanelHolder.showPanel(new Panel(R.drawable.terminal, "Terminal", TerminalFragment.class));
    }

    public void showOrHideNoFileSelectedMessage() {
        if(codeEditorPagerAdapter.getPaths().isEmpty()) {
            codeEditorSwitcher.setDisplayedChild(1);
        } else {
            codeEditorSwitcher.setDisplayedChild(0);
        }
    }

    public void makeDraggable(View divider, View viewA, View viewB, boolean horizontal){
        divider.setOnTouchListener(new View.OnTouchListener() {
            float initialPos = 0;
            boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialPos = horizontal ? event.getRawX() : event.getRawY();
                        isDragging = true;

                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            LinearLayout.LayoutParams paramsA = (LinearLayout.LayoutParams) viewA.getLayoutParams();
                            LinearLayout.LayoutParams paramsB = (LinearLayout.LayoutParams) viewB.getLayoutParams();

                            int total = horizontal ? ((View) viewA.getParent()).getWidth() : ((View) viewA.getParent()).getHeight();
                            float newAWeight = (horizontal ? event.getRawX() : event.getRawY()) / total;
                            float newBWeight = 1 - newAWeight;

                            paramsA.weight = newAWeight;
                            paramsB.weight = newBWeight;

                            viewA.setLayoutParams(paramsA);
                            viewB.setLayoutParams(paramsB);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isDragging = false;
                        return true;

                    default:
                        return false;
                }
            }
        });


        divider.setOnHoverListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                v.setPointerIcon(PointerIcon.getSystemIcon(MainActivity.this, horizontal ? PointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW : PointerIcon.TYPE_VERTICAL_DOUBLE_ARROW));
            } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                v.setPointerIcon(PointerIcon.getSystemIcon(MainActivity.this, PointerIcon.TYPE_DEFAULT));
            }
            return false;
        });
    }

    @Override
    public void onFileTreeNodeSelected(FileTreeNode fileTreeNode) {
        String path = fileTreeNode.getFile().getAbsolutePath();
        if(fileTreeNode.getFile().isFile()) {
            if(!codeEditorPagerAdapter.getPaths().contains(path)) {
                codeEditorPagerAdapter.getPaths().add(path);
                codeEditorPagerAdapter.notifyDataSetChanged();
                codeEditorViewPager.setCurrentItem(codeEditorPagerAdapter.getPaths().size() - 1);
            } else {
                codeEditorViewPager.setCurrentItem(codeEditorPagerAdapter.getPaths().indexOf(path));
            }
        }
    }

}