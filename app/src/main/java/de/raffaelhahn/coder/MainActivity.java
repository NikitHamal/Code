package de.raffaelhahn.coder;

import static androidx.core.view.ViewCompat.setSystemGestureExclusionRects;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import de.raffaelhahn.coder.filetree.FileTreeCallback;
import de.raffaelhahn.coder.filetree.FileTreeFragment;
import de.raffaelhahn.coder.filetree.FileTreeNode;
import de.raffaelhahn.coder.editor.CodeEditorPagerAdapter;
import de.raffaelhahn.coder.terminal.Terminal;

public class MainActivity extends AppCompatActivity implements FileTreeCallback {

    private FragmentContainerView fileTreeContainer;
    private FragmentContainerView terminalContainer;
    private ViewPager2 codeEditorViewPager;
    private CodeEditorPagerAdapter codeEditorPagerAdapter;
    private FileTreeFragment fileTreeFragment;
    private TabLayout editorTabs;

    private String path;



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



        Bundle b = getIntent().getExtras();
        path = b.getString("path");

        codeEditorPagerAdapter = new CodeEditorPagerAdapter(this);

        fileTreeContainer = findViewById(R.id.fileTreeContainer);
        terminalContainer = findViewById(R.id.terminalContainer);
        codeEditorViewPager = findViewById(R.id.codeEditorViewPager);
        editorTabs = findViewById(R.id.codeEditorTabs);
        codeEditorViewPager.setAdapter(codeEditorPagerAdapter);

        fileTreeFragment = FileTreeFragment.newInstance(path);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fileTreeContainer, fileTreeFragment)
                .commit();

        new TabLayoutMediator(editorTabs, codeEditorViewPager, (tab, position) -> {
            tab.setCustomView(R.layout.editor_tab_item);
            TextView titleView = tab.getCustomView().findViewById(R.id.editorTabText);
            titleView.setText(codeEditorPagerAdapter.getPaths().get(position));
            tab.setText(codeEditorPagerAdapter.getPaths().get(position));
            tab.getCustomView().findViewById(R.id.editorTabCloseButton).setOnClickListener(v -> {
                codeEditorPagerAdapter.getPaths().remove(position);
                codeEditorPagerAdapter.notifyDataSetChanged();
            });
        }).attach();
        codeEditorViewPager.setUserInputEnabled(false);
        makeDraggable(findViewById(R.id.divider1), fileTreeContainer, findViewById(R.id.rightPane), true);
        makeDraggable(findViewById(R.id.divider2), (View) codeEditorViewPager.getParent(), terminalContainer, false);
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
            return false; // Ereignis weiterleiten
        });
    }

    @Override
    public void onFileTreeNodeSelected(FileTreeNode fileTreeNode) {
        String path = fileTreeNode.getFile().getAbsolutePath();
        if(fileTreeNode.getFile().isFile()) {
            if(codeEditorPagerAdapter.getPaths().indexOf(path) == -1) {
                codeEditorPagerAdapter.getPaths().add(path);
                codeEditorPagerAdapter.notifyDataSetChanged();
                codeEditorViewPager.setCurrentItem(codeEditorPagerAdapter.getPaths().size() - 1);
            } else {
                codeEditorViewPager.setCurrentItem(codeEditorPagerAdapter.getPaths().indexOf(path));
            }
        }
    }


    @Override
    public void onFileTreeNodeDeleteTriggered(FileTreeNode fileTreeNode) {

    }

    @Override
    public void onFileTreeNodeRenameTriggered(FileTreeNode fileTreeNode, String newName) {

    }

    @Override
    public void onFileTreeNodeCreateTriggered(FileTreeNode parentFileTreeNode, String fileName) {

    }
}