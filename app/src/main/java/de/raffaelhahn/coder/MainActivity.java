package de.raffaelhahn.coder;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import java.io.File;

import de.raffaelhahn.coder.ui.CodeEditorFragment;
import de.raffaelhahn.coder.ui.FileTreeFragment;

public class MainActivity extends AppCompatActivity implements FileTreeFragment.FileTreeCallback {

    private FragmentContainerView fileTreeContainer;
    private FragmentContainerView codeEditorContainer;
    private FileTreeFragment fileTreeFragment;



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

        fileTreeContainer = findViewById(R.id.fileTreeContainer);
        codeEditorContainer = findViewById(R.id.codeEditorContainer);

        fileTreeFragment = new FileTreeFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fileTreeContainer, fileTreeFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.codeEditorContainer, CodeEditorFragment.class, null)
                .commit();


    }

    @Override
    public void onFileSelected(String path) {

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