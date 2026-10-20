package com.gradesystem.exception;

/**
 * Thrown when a file cannot be written (disk full, no permission, ...).
 * It wraps the original {@link java.io.IOException} so nothing is lost.
 */
public class FileExportException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new FileExportException.
     *
     * @param fileName the file that could not be written
     * @param cause    the original I/O error
     */
    public FileExportException(String fileName, Throwable cause) {
        super("Could not write file '" + fileName + "'.",
                "Check that the folder exists and you have permission to write there.", cause);
    }
}
