package com.gradesystem.exception;

import com.gradesystem.util.AppConstants;

/**
 * Thrown when an import file does not have the expected CSV layout
 * (empty file, wrong header, no data rows).
 */
public class InvalidFileFormatException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidFileFormatException.
     *
     * @param fileName the file that has the problem
     * @param reason   what exactly is wrong with it
     */
    public InvalidFileFormatException(String fileName, String reason) {
        super("File '" + fileName + "' has an invalid format: " + reason,
                "The first line must be: " + AppConstants.CSV_HEADER);
    }
}
