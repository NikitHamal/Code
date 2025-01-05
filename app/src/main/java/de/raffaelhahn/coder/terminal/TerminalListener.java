package de.raffaelhahn.coder.terminal;

public interface TerminalListener {

    void onOutput(Terminal terminal, String line);
    void onExit(Terminal terminal, int exitCode);
}
