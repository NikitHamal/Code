package de.raffaelhahn.coder.terminal.termux;

import android.annotation.SuppressLint;

import java.io.File;

public class TermuxConstants {

    /**
     * Termux package name - In this case it's the package name of coder
     */
    public static final String TERMUX_PACKAGE_NAME = "de.raffaelhahn.coder"; // Default: "com.termux"

    /**
     * Termux app Files directory path
     */
    @SuppressLint("SdCardPath")
    public static final String TERMUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH = "/data/data/" + TERMUX_PACKAGE_NAME; // Default: "/data/data/com.termux"

    /**
     * Termux app usr-staging directory path
     */
    public static final String TERMUX_FILES_DIR_PATH = TERMUX_INTERNAL_PRIVATE_APP_DATA_DIR_PATH + "/files"; // Default: "/data/data/com.termux/files"

    /**
     * Termux app internal private app data directory path
     */
    public static final String TERMUX_STAGING_PREFIX_DIR_PATH = TERMUX_FILES_DIR_PATH + "/usr-staging"; // Default: "/data/data/com.termux/files/usr-staging"

    /**
     * Termux app usr-staging directory
     */
    public static final File TERMUX_STAGING_PREFIX_DIR = new File(TERMUX_STAGING_PREFIX_DIR_PATH);

    /**
     * Termux app $PREFIX directory path
     */
    public static final String TERMUX_PREFIX_DIR_PATH = TERMUX_FILES_DIR_PATH + "/usr"; // Default: "/data/data/com.termux/files/usr"
    /**
     * Termux app $PREFIX directory
     */
    public static final File TERMUX_PREFIX_DIR = new File(TERMUX_PREFIX_DIR_PATH);

    /**
     * Termux app $HOME directory path
     */
    public static final String TERMUX_HOME_DIR_PATH = TERMUX_FILES_DIR_PATH + "/home";

    /**
     * Termux app $PREFIX/tmp and $TMPDIR directory path
     */
    public static final String TERMUX_TMP_PREFIX_DIR_PATH = TERMUX_PREFIX_DIR_PATH + "/tmp";

    /**
     * Termux app $PREFIX/bin directory path
     */
    public static final String TERMUX_BIN_PREFIX_DIR_PATH = TERMUX_PREFIX_DIR_PATH + "/bin"; // Default: "/data/data/com.termux/files/usr/bin"

}
