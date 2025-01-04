package de.raffaelhahn.coder;

import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;

import de.raffaelhahn.coder.ui.CodeEditorFragment;
import de.raffaelhahn.coder.ui.FileTreeCallback;
import de.raffaelhahn.coder.ui.FileTreeFragment;
import de.raffaelhahn.coder.ui.adapters.CodeEditorPagerAdapter;

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
    }

    @Override
    public void onFileSelected(String path) {
        File file = new File(path);
        if(file.isFile()) {
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
    public void onFileDeleted(String path) {

    }

    @Override
    public void onFileRenamed(String path, String newName) {

    }

    @Override
    public void onFileCreated(String path) {

    }

}