package de.raffaelhahn.coder.terminal;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

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
    @Getter
    private Process process;

    /**
     * Konstruktor
     *
     * @param directory   e.g. context.getFilesDir()
     * @param application CoderApp
     */
    public Terminal(File directory, CoderApp application) {
        this.application = application;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(directory);
        processBuilder.environment().put("Path", "/bin");
    }

    public void runCommand(String...commandAndArgs) {
        if (process != null) {
            return;
        }
        Executor executor = application.getExecutorService();
        executor.execute(() -> {
            String errorMessage;
            try {
                String[] bashCommand = Stream.concat(Stream.of("/bin/sh", "-c"), Stream.of(commandAndArgs)).toArray(String[]::new);
                processBuilder.command(bashCommand);
                process = processBuilder.start();
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
                errorMessage = exitCode == 0 ? null : "Process failed with exit code: " + exitCode;
            } catch (IOException | InterruptedException e) {
                errorMessage = e.getMessage();
            }
            process = null;
            if (listener != null) {
                listener.onExit(Terminal.this, errorMessage);
            }
        });
    }

    public void cancel() {
        if (process != null) {
            if(process.isAlive()) {
                process.destroyForcibly();
            }
            process = null;
        }
    }
}
