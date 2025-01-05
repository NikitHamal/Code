package de.raffaelhahn.coder.terminal;

import androidx.annotation.Nullable;

public interface TerminalListener {

    void onOutput(Terminal terminal, String line);
    void onExit(Terminal terminal, @Nullable String errorMessage);
}
