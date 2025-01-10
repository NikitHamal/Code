package de.raffaelhahn.coder.terminal;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.raffaelhahn.coder.CoderApp;
import de.raffaelhahn.coder.terminal.termux.FileUtils;
import de.raffaelhahn.coder.terminal.termux.TermuxConstants;
import lombok.Getter;
import lombok.Setter;

public class Terminal {

    private CoderApp application;
    private String[] environment;

    @Getter
    private TerminalSessionClient terminalSessionClient;
    @Getter
    private TerminalSession terminalSession;
    @Setter
    private TerminalView terminalView;

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

        environment = setupEnvironment(false);

        var shellFile = new File(TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH, "login");
        if (shellFile.isFile()) {
            if (!shellFile.canExecute()) {
                if (!shellFile.setExecutable(true)) {
                    Log.e("Terminal", "Cannot set executable: " + shellFile.getAbsolutePath());
                }
            }
        } else {
            Log.e("Terminal", "bin/login not found");
        }
        ExecuteCommand command = setupShellCommandArguments(shellFile, new String[]{}, true);

        terminalSessionClient = new TerminalSessionClient() {
            @Override
            public void onTextChanged(@NonNull TerminalSession changedSession) {
                if(terminalView != null) terminalView.onScreenUpdated();
            }

            @Override
            public void onTitleChanged(@NonNull TerminalSession changedSession) {

            }

            @Override
            public void onSessionFinished(@NonNull TerminalSession finishedSession) {

            }

            @Override
            public void onCopyTextToClipboard(@NonNull TerminalSession session, String text) {

            }

            @Override
            public void onPasteTextFromClipboard(@Nullable TerminalSession session) {

            }

            @Override
            public void onBell(@NonNull TerminalSession session) {

            }

            @Override
            public void onColorsChanged(@NonNull TerminalSession session) {

            }

            @Override
            public void onTerminalCursorStateChange(boolean state) {

            }
        };

        terminalSession = new TerminalSession(
                command.executablePath,
                TermuxConstants.HOME_PATH,
                command.arguments,
                environment,
                4000,
                terminalSessionClient
        );
    }

    public static String[] setupEnvironment(boolean failsafe) {
        String tmpDir = TermuxConstants.TERMUX_PREFIX_DIR_PATH + "/tmp";

        Map<String, String> environment = new HashMap<>();
        environment.put("COLORTERM", "truecolor");
        environment.put("PREFIX", TermuxConstants.TERMUX_PREFIX_DIR_PATH);
        environment.put("TERM", "xterm-256color");
        //environment.put("TERMUX_VERSION", BuildConfig.VERSION_NAME);
        putToEnvIfInSystemEnv(environment, "ANDROID_ART_ROOT");
        putToEnvIfInSystemEnv(environment, "ANDROID_ASSETS");
        putToEnvIfInSystemEnv(environment, "ANDROID_DATA");
        putToEnvIfInSystemEnv(environment, "ANDROID_I18N_ROOT");
        putToEnvIfInSystemEnv(environment, "ANDROID_ROOT");
        putToEnvIfInSystemEnv(environment, "ANDROID_RUNTIME_ROOT");
        putToEnvIfInSystemEnv(environment, "ANDROID_STORAGE");
        putToEnvIfInSystemEnv(environment, "ANDROID_TZDATA_ROOT");
        putToEnvIfInSystemEnv(environment, "ASEC_MOUNTPOINT");
        putToEnvIfInSystemEnv(environment, "BOOTCLASSPATH");
        putToEnvIfInSystemEnv(environment, "DEX2OATBOOTCLASSPATH");
        putToEnvIfInSystemEnv(environment, "EXTERNAL_STORAGE");
        putToEnvIfInSystemEnv(environment, "LOOP_MOUNTPOINT");
        putToEnvIfInSystemEnv(environment, "SYSTEMSERVERCLASSPATH");

        if (!failsafe) {
            environment.put("HOME", TermuxConstants.TERMUX_HOME_DIR_PATH);
            environment.put("LANG", "en_US.UTF-8");
            environment.put("TMP", tmpDir);
            environment.put("TMPDIR", tmpDir);
            environment.put("LD_PRELOAD", TermuxConstants.TERMUX_PREFIX_DIR_PATH + "/lib/libtermux-exec.so");
            environment.put("PATH", TermuxConstants.TERMUX_PREFIX_DIR_PATH + "/bin:" + System.getenv("PATH"));
        }

        List<String> environmentList = new ArrayList<>(environment.size());
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            environmentList.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(environmentList);
        return environmentList.toArray(new String[0]);
    }

    @NonNull
    public static ExecuteCommand setupShellCommandArguments(@NonNull File executable, @NonNull String[] arguments, boolean isLoginShell) {
        // The file to execute may either be:
        // - An elf file, in which we execute it directly.
        // - A script file without shebang, which we execute with our standard shell $PREFIX/bin/sh instead of the
        //   system /system/bin/sh. The system shell may vary and may not work at all due to LD_LIBRARY_PATH.
        // - A file with shebang, which we try to handle with e.g. /bin/foo -> $PREFIX/bin/foo.
        String interpreter = null;
        try (FileInputStream in = new FileInputStream(executable)) {
            byte[] buffer = new byte[256];
            int bytesRead = in.read(buffer);
            if (bytesRead > 4) {
                if (buffer[0] == 0x7F && buffer[1] == 'E' && buffer[2] == 'L' && buffer[3] == 'F') {
                    // Elf file, do nothing.
                } else if (buffer[0] == '#' && buffer[1] == '!') {
                    // Try to parse shebang.
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < bytesRead; i++) {
                        char c = (char) buffer[i];
                        if (c == ' ' || c == '\n') {
                            if (builder.length() == 0) {
                                // Skip whitespace after shebang.
                            } else {
                                // End of shebang.
                                String shebangExecutable = builder.toString();
                                if (shebangExecutable.startsWith("/usr") || shebangExecutable.startsWith("/bin")) {
                                    String[] parts = shebangExecutable.split("/");
                                    String binary = parts[parts.length - 1];
                                    interpreter = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/" + binary;
                                } else if (shebangExecutable.startsWith(TermuxConstants.TERMUX_FILES_DIR_PATH)) {
                                    interpreter = shebangExecutable;
                                }
                                break;
                            }
                        } else {
                            builder.append(c);
                        }
                    }
                } else {
                    // No shebang and no ELF, use standard shell.
                    interpreter = TermuxConstants.TERMUX_BIN_PREFIX_DIR_PATH + "/sh";
                }
            }
        } catch (IOException e) {
            Log.e("Terminal", "IO exception", e);
        }

        var elfFileToExecute = interpreter == null ? executable.getAbsolutePath() : interpreter;

        var actualArguments = new ArrayList<>();
        var processName = (isLoginShell ? "-" : "") + executable.getName();
        actualArguments.add(processName);

        String actualFileToExecute;
        if (elfFileToExecute.startsWith(TermuxConstants.TERMUX_FILES_DIR_PATH)) {
            actualFileToExecute = "/system/bin/linker" + (android.os.Process.is64Bit() ? "64" : "");
            actualArguments.add(elfFileToExecute);
        } else {
            actualFileToExecute = elfFileToExecute;
        }

        if (interpreter != null) {
            actualArguments.add(executable.getAbsolutePath());
        }
        Collections.addAll(actualArguments, arguments);
        return new ExecuteCommand(actualFileToExecute, actualArguments.toArray(new String[0]));
    }

    private static void putToEnvIfInSystemEnv(@NonNull Map<String, String> environment, @NonNull String name) {
        String value = System.getenv(name);
        if (value != null) {
            environment.put(name, value);
        }
    }

    public void runCommand(String command) {
        byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
        terminalSession.write(bytes, 0, bytes.length);
        //terminalSession.getEmulator().getScreen().getTranscriptText();
    }

    public void cancel() {

    }

    public static class ExecuteCommand {
        public ExecuteCommand(String executablePath, String[] arguments) {
            this.executablePath = executablePath;
            this.arguments = arguments;
        }

        final String executablePath;
        final String[] arguments;
    }

}
