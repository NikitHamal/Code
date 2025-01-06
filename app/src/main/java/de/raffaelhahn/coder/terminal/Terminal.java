package de.raffaelhahn.coder.terminal;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.terminal.termux.FileUtils;
import de.raffaelhahn.coder.terminal.termux.TermuxConstants;
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
     * @param application CoderApp
     */
    public Terminal(CoderApp application) {
        File homeDir = new File(TermuxConstants.TERMUX_HOME_DIR_PATH);
        if(!homeDir.exists()) {
            FileUtils.createDirectoryFile("shell home", TermuxConstants.TERMUX_HOME_DIR_PATH);
        }
        this.application = application;
        processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(new File(TermuxConstants.TERMUX_HOME_DIR_PATH)); //new File(directory, "usr")
        //processBuilder.environment().put("Path", "/data/data/de.raffaelhahn.coder/files/usr/bin");
        processBuilder.environment().put("HOME", TermuxConstants.TERMUX_HOME_DIR_PATH);
        processBuilder.environment().put("PREFIX",  TermuxConstants.TERMUX_PREFIX_DIR_PATH);

        processBuilder.environment().put("TMPDIR", TermuxConstants.TERMUX_TMP_PREFIX_DIR_PATH);
        processBuilder.environment().put("PATH", TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH);
        processBuilder.environment().remove("LD_LIBRARY_PATH");
    }

    public void runCommand(String...commandAndArgs) {
        if (process != null) {
            return;
        }
        Executor executor = application.getExecutorService();
        executor.execute(() -> {
            String errorMessage;
            try {
                String[] bashCommand = new String[]{
                        //"/bin/bash",
                        //"-c",
                        //"'"+String.join(" ", commandAndArgs)+"'"
                        String.join(" ", commandAndArgs)
                };
                //String[] bashCommand = Stream.concat(Stream.of("/bin/sh", "-c"), Stream.of(commandAndArgs)).toArray(String[]::new);
                processBuilder.command(commandAndArgs);
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
