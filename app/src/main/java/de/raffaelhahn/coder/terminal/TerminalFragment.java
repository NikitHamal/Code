package de.raffaelhahn.coder.terminal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.termux.terminal.TerminalSession;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.MainActivity;
import de.raffaelhahn.coder.R;

public class TerminalFragment extends Fragment implements TerminalViewClient {

    private TerminalView terminalView;
    private Terminal terminal;

    public TerminalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        terminal = ((MainActivity) getActivity()).getTerminal();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terminal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        terminalView = view.findViewById(R.id.terminal_view);

        view.post(new Runnable() {
            @Override
            public void run() {
                terminalView.setTerminalViewClient(TerminalFragment.this);
                terminalView.setTextSize(16);
                terminalView.attachSession(terminal.getTerminalSession());
                terminal.setTerminalView(terminalView);
            }
        });
    }

    @Override
    public float onScale(float scale) {
        return 1.0f;
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        if (!terminal.getTerminalSession().getEmulator().isMouseTrackingActive() && !e.isFromSource(InputDevice.SOURCE_MOUSE)) {
            getActivity().getSystemService(InputMethodManager.class).showSoftInput(terminalView, 0);
        }
    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return false;
    }

    @Override
    public boolean isTerminalViewSelected() {
        return terminalView.hasFocus();
    }

    @Override
    public void copyModeChanged(boolean copyMode) {

    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        return false;
    }

    @Override
    public boolean onLongPress(MotionEvent event) {
        return false;
    }

    @Override
    public boolean readControlKey() {
        return false;
    }

    @Override
    public boolean readAltKey() {
        return false;
    }

    @Override
    public boolean readShiftKey() {
        return false;
    }

    @Override
    public boolean readFnKey() {
        return false;
    }

    @Override
    public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
        return false;
    }
}