package de.raffaelhahn.coder.intro;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import de.raffaelhahn.coder.R;
import de.raffaelhahn.coder.terminal.TerminalInstaller;

public class IntroTerminalInstallationFragment extends Fragment {

    private Button button;
    private LinearProgressIndicator progressIndicator;
    private TextView mainTextView;
    private IntroActivity activity;

    public IntroTerminalInstallationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_terminal_installation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.button);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        mainTextView = view.findViewById(R.id.welcomeTextView);
    }

    @Override
    public void onResume() {
        super.onResume();

        TerminalInstaller terminalInstaller = new TerminalInstaller();
        terminalInstaller.setupAppLibSymlink(requireContext());
        progressIndicator.setIndeterminate(true);
        terminalInstaller.install(new TerminalInstaller.TerminalInstallerListener() {
            @Override
            public void onTerminalInstallerDone() {
                requireActivity().runOnUiThread(() -> {
                    progressIndicator.setIndeterminate(false);
                    progressIndicator.setProgress(100);
                    button.setEnabled(true);
                    button.setOnClickListener(v -> activity.nextFragment());
                    mainTextView.setText(R.string.intro_terminal_install_text_done);
                });
            }

            @Override
            public void onTerminalInstallerFailed(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressIndicator.setIndeterminate(false);
                    progressIndicator.setProgress(0);

                    new AlertDialog.Builder(requireContext())
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle("Error")
                            .setMessage(error)
                            .show();
                });
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof IntroActivity io) {
            activity = io;
        }
    }
}