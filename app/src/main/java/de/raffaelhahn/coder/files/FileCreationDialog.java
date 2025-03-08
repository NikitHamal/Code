package de.raffaelhahn.coder.files;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import de.raffaelhahn.coder.R;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileCreationDialog extends DialogFragment {

    private EditText editText;
    private String path;
    private boolean directory;

    public static FileCreationDialog newInstance(String path, boolean directory) {
        FileCreationDialog f = new FileCreationDialog();

        Bundle args = new Bundle();
        args.putString("path", path);
        args.putBoolean("directory", directory);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_file_creation, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        path = getArguments().getString("path", null);
        directory = getArguments().getBoolean("directory", false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.cancelButton).setOnClickListener(view1 -> dismiss());

        TextInputLayout textInputLayout = view.findViewById(R.id.fileName);

        editText = view.findViewById(R.id.fileNameEditText);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createFile();
                return true;
            }
            return false;
        });

        view.findViewById(R.id.saveButton).setOnClickListener(view1 -> createFile());

        if(directory) {
            textInputLayout.setHint(R.string.directory_name);
            toolbar.setTitle(R.string.create_new_directory);
        } else {
            textInputLayout.setHint(R.string.file_name);
            toolbar.setTitle(R.string.create_new_file);
        }

    }

    public void createFile() {
        String fileName = editText.getText().toString();
        String error = FileManager.createFile(requireContext(), path + "/" + fileName, directory);

        if(error != null) {
            new MaterialAlertDialogBuilder(new ContextThemeWrapper(requireContext(), R.style.Theme_Coder), R.style.MaterialAlertDialogCenterStyle)
                    .setTitle(R.string.file_creation_error_title)
                    .setMessage(requireContext().getString(R.string.file_creation_error_description, error))
                    .setIcon(R.drawable.error)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {})
                    .create()
                    .show();
        }
        dismiss();
    }
}
