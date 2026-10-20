package com.gradesystem.exception;

/**
 * Thrown when user input breaks a rule: a bad name, email, age,
 * file name, search range, or a CSV row with the wrong number of columns.
 */
public class InvalidInputException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidInputException.
     *
     * @param message      what is wrong with the input
     * @param recoveryHint an example of correct input
     */
    public InvalidInputException(String message, String recoveryHint) {
        super(message, recoveryHint);
    }
}
