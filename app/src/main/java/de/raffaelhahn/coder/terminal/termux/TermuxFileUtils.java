package de.raffaelhahn.coder.terminal.termux;

public class TermuxFileUtils {

    /**
     * Validate if {@link TermuxConstants#TERMUX_PREFIX_DIR_PATH} exists and has
     * {@link FileUtils#APP_WORKING_DIRECTORY_PERMISSIONS} permissions.
     * .
     *
     * The {@link TermuxConstants#TERMUX_PREFIX_DIR_PATH} directory would not exist if termux has
     * not been installed or the bootstrap setup has not been run or if it was deleted by the user.
     *
     * @param createDirectoryIfMissing The {@code boolean} that decides if directory file
     *                                 should be created if its missing.
     * @param setMissingPermissions The {@code boolean} that decides if permissions are to be
     *                              automatically set.
     * @return Returns the {@code error} if path is not a directory file, failed to create it,
     * or validating permissions failed, otherwise {@code null}.
     */
    public static Error isTermuxPrefixDirectoryAccessible(boolean createDirectoryIfMissing, boolean setMissingPermissions) {
        return FileUtils.validateDirectoryFileExistenceAndPermissions("termux prefix directory", TermuxConstants.TERMUX_PREFIX_DIR_PATH,
                null, createDirectoryIfMissing,
                FileUtils.APP_WORKING_DIRECTORY_PERMISSIONS, setMissingPermissions, true,
                false, false);
    }

    /**
     * Validate if {@link TermuxConstants#TERMUX_STAGING_PREFIX_DIR_PATH} exists and has
     * {@link FileUtils#APP_WORKING_DIRECTORY_PERMISSIONS} permissions.
     *
     * @param createDirectoryIfMissing The {@code boolean} that decides if directory file
     *                                 should be created if its missing.
     * @param setMissingPermissions The {@code boolean} that decides if permissions are to be
     *                              automatically set.
     * @return Returns the {@code error} if path is not a directory file, failed to create it,
     * or validating permissions failed, otherwise {@code null}.
     */
    public static Error isTermuxPrefixStagingDirectoryAccessible(boolean createDirectoryIfMissing, boolean setMissingPermissions) {
        return FileUtils.validateDirectoryFileExistenceAndPermissions("termux prefix staging directory", TermuxConstants.TERMUX_STAGING_PREFIX_DIR_PATH,
                null, createDirectoryIfMissing,
                FileUtils.APP_WORKING_DIRECTORY_PERMISSIONS, setMissingPermissions, true,
                false, false);
    }


}