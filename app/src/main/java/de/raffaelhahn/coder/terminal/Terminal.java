package de.raffaelhahn.coder.terminal;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;

import de.raffaelhahn.coder.CoderApp;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class Terminal {

    private ProcessBuilder processBuilder;
    @Getter
    private StringBuilder output = new StringBuilder();
    @Setter
    private TerminalListener listener;
    private CoderApp application;

    /**
     * Konstruktor
     * @param directory e.g. context.getFilesDir()
     */
    public Terminal(File directory, CoderApp application) {
        this.application = application;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(directory);
    }

    public void runCommand(String...commandAndArgs) {
        Executor executor = application.getExecutorService();
        executor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                processBuilder.command(commandAndArgs);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (listener != null) {
                        listener.onOutput(Terminal.this, line);
                    }
                }

                int exitCode = process.waitFor();
                Log.d("TERMINAL", "Exit code: " + exitCode);
                if (listener != null) {
                    listener.onExit(Terminal.this, exitCode);
                }
            }
        });
    }
}
