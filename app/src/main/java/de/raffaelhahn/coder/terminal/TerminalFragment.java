package de.raffaelhahn.coder.terminal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.R;

public class TerminalFragment extends Fragment implements TerminalListener {

    private TextView terminalOutput;
    private EditText terminalInput;
    private CircularProgressIndicator terminalProgress;
    private Terminal terminal;

    public TerminalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        terminal = ((CoderApp) getActivity().getApplication()).getTerminal();
        terminal.setListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terminal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        terminalOutput = view.findViewById(R.id.terminalOutput);
        terminalInput = view.findViewById(R.id.terminalInput);
        terminalProgress = view.findViewById(R.id.terminalProgress);

        terminalInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendCommand();
                    return true;
                }
                return false;
            }
        });

        terminalProgress.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            MenuItem stopProcessItem = popupMenu.getMenu().add("Stop process");
            popupMenu.setOnMenuItemClickListener(item -> {
                if(stopProcessItem == item) {
                    terminal.cancel();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    public void sendCommand() {
        terminalInput.setEnabled(false);
        terminalProgress.setVisibility(View.VISIBLE);
        terminal.runCommand(terminalInput.getText().toString().split(" "));
        terminalInput.setText("");
    }

    @Override
    public void onOutput(Terminal terminal, String line) {
        requireActivity().runOnUiThread(() -> {
            terminalOutput.append(line);
            terminalOutput.append("\n");
        });
    }

    @Override
    public void onExit(Terminal terminal, @Nullable String errorMessage) {
        requireActivity().runOnUiThread(() -> {
            if(errorMessage != null) {
                terminalOutput.append(errorMessage);
                terminalOutput.append("\n");
            }
            terminalInput.setEnabled(true);
            terminalProgress.setVisibility(View.GONE);
        });
    }
}