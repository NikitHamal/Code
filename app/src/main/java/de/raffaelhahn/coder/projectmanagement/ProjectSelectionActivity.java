package de.raffaelhahn.coder.projectmanagement;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.MainActivity;
import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.intro.IntroActivity;
import de.raffaelhahn.coder.utils.Utils;

public class ProjectSelectionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DIRECTORY_PICKER = 10;
    private MaterialButton openButton;
    private RecyclerView projectsRecyclerView;
    private ProjectsAdapter projectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!Utils.checkStoragePermissions(this)) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }

        openButton = findViewById(R.id.projectSelectionOpenButton);
        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE_DIRECTORY_PICKER);
        });

        projectsRecyclerView = findViewById(R.id.projectSelectionRecyclerView);
        projectsAdapter = new ProjectsAdapter(this::openProject);
        ArrayList<Project> projects = new ArrayList<>(ProjectsStorage.getProjects(this));
        projects.sort((o1, o2) -> {
            if (o1.lastOpened == null) {
                return 1;
            }
            if (o2.lastOpened == null) {
                return -1;
            }
            return o1.lastOpened.compareTo(o2.lastOpened) * -1;
        });
        projectsAdapter.setProjects(projects);
        projectsRecyclerView.setAdapter(projectsAdapter);
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DIRECTORY_PICKER && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();

            // Persist access permissions
            getContentResolver().takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            // Resolve file system path
            String fileSystemPath = getFileSystemPath(treeUri);
            Log.d("SelectedDirectory", "Path: " + fileSystemPath);

            Project project = ProjectsStorage.addProject(this, new Project(fileSystemPath, LocalDateTime.now()));

            openProject(project);
        }
    }

    public void openProject(Project project) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("path", project.getPath());
        ((CoderApp) getApplication()).getFileAppContext().setCurrentProject(project);
        startActivity(i);
    }

    private String getFileSystemPath(Uri uri) {
        if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
            String docId = DocumentsContract.getTreeDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {
                // Handle non-primary volumes (e.g., SD cards or USB storage)
                String[] externalStoragePaths = getExternalStoragePaths();
                for (String path : externalStoragePaths) {
                    if (path.contains(type)) {
                        return path + "/" + split[1];
                    }
                }
            }
        }
        return null;
    }

    private String[] getExternalStoragePaths() {
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
        String[] paths = new String[externalStorageFiles.length];
        for (int i = 0; i < externalStorageFiles.length; i++) {
            paths[i] = externalStorageFiles[i].getAbsolutePath()
                    .replace("/Android/data/" + getPackageName() + "/files", "");
        }
        return paths;
    }


}