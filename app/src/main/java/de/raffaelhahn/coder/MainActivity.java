package de.raffaelhahn.coder;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.tabs.TabLayout;

import java.io.File;

import de.raffaelhahn.coder.ui.CodeEditorFragment;
import de.raffaelhahn.coder.ui.FileTreeCallback;
import de.raffaelhahn.coder.ui.FileTreeFragment;

public class MainActivity extends AppCompatActivity implements FileTreeCallback {

    private FragmentContainerView fileTreeContainer;
    private FragmentContainerView codeEditorContainer;
    private FileTreeFragment fileTreeFragment;
    private CodeEditorFragment codeEditorFragment;
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

        fileTreeContainer = findViewById(R.id.fileTreeContainer);
        codeEditorContainer = findViewById(R.id.codeEditorContainer);
        editorTabs = findViewById(R.id.codeEditorTabs);

        editorTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String path = (String) tab.getTag();

                codeEditorFragment.loadFile(path);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fileTreeFragment = FileTreeFragment.newInstance(path);
        codeEditorFragment = CodeEditorFragment.newInstance(null);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fileTreeContainer, fileTreeFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.codeEditorContainer, codeEditorFragment)
                .commit();
    }

    @Override
    public void onFileSelected(String path) {
        File file = new File(path);
        if(file.isFile()) {
            TabLayout.Tab tab = null;
            for (int i = 0; i < editorTabs.getTabCount(); i++) {
                if (editorTabs.getTabAt(i).getTag().equals(path)) {
                    tab = editorTabs.getTabAt(i);
                    break;
                }
            }
            if (tab == null) {
                tab = editorTabs.newTab();
                tab.setText(file.getName());
                tab.setTag(path);
                editorTabs.addTab(tab);
            }
            editorTabs.selectTab(tab);
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