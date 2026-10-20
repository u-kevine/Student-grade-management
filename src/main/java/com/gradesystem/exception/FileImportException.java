package com.gradesystem.exception;

import com.gradesystem.util.AppConstants;

/**
 * Thrown when an import file cannot be found or read.
 */
public class FileImportException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Used when the file simply does not exist.
     *
     * @param fileName the missing file
     */
    public FileImportException(String fileName) {
        super("File '" + fileName + "' was not found.",
                "Place the CSV file in the ./" + AppConstants.IMPORTS_DIR + "/ folder and type its name without .csv");
    }

    /**
     * Used when the file exists but reading it failed.
     *
     * @param fileName the file
     * @param cause    the original I/O error
     */
    public FileImportException(String fileName, Throwable cause) {
        super("File '" + fileName + "' could not be read.",
                "Make sure the file is not open in another program.", cause);
    }
}
