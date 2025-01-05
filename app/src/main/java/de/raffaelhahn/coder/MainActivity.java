package de.raffaelhahn.coder;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.raffaelhahn.coder.filetree.FileTreeCallback;
import de.raffaelhahn.coder.filetree.FileTreeFragment;
import de.raffaelhahn.coder.filetree.FileTreeNode;
import de.raffaelhahn.coder.editor.CodeEditorPagerAdapter;
import de.raffaelhahn.coder.terminal.Terminal;

public class MainActivity extends AppCompatActivity implements FileTreeCallback {

    private FragmentContainerView fileTreeContainer;
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
        makeDraggable(findViewById(R.id.divider), fileTreeContainer, (View)codeEditorViewPager.getParent());
    }

    public void makeDraggable(View divider, View viewA, View viewB){
        divider.setOnTouchListener(new View.OnTouchListener() {
            float initialX = 0;
            boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getRawX();
                        isDragging = true;

                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            LinearLayout.LayoutParams paramsLeft = (LinearLayout.LayoutParams) viewA.getLayoutParams();
                            LinearLayout.LayoutParams paramsRight = (LinearLayout.LayoutParams) viewB.getLayoutParams();

                            int totalWidth = ((View) viewA.getParent()).getWidth();
                            float newLeftWeight = event.getRawX() / totalWidth;
                            float newRightWeight = 1 - newLeftWeight;

                            paramsLeft.weight = newLeftWeight;
                            paramsRight.weight = newRightWeight;

                            viewA.setLayoutParams(paramsLeft);
                            viewB.setLayoutParams(paramsRight);
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
                v.setPointerIcon(PointerIcon.getSystemIcon(MainActivity.this, PointerIcon.TYPE_HORIZONTAL_DOUBLE_ARROW));
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