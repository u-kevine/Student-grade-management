package com.gradesystem.exception;

import com.gradesystem.util.TextFormat;

/**
 * Thrown when a grade is not a number or is outside 0-100.
 */
public class InvalidGradeException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /** Exactly what was typed or read from the file. */
    private final String enteredValue;

    /**
     * Used when the grade is a number but out of range (for example 150).
     *
     * @param grade the wrong grade
     */
    public InvalidGradeException(double grade) {
        super("Grade must be between 0 and 100.", "You entered: " + TextFormat.number(grade));
        this.enteredValue = TextFormat.number(grade);
    }

    /**
     * Used when the grade is not a number at all (for example "abc").
     *
     * @param text the text that was entered
     */
    public InvalidGradeException(String text) {
        super("Grade must be a number between 0 and 100.", "You entered: " + text);
        this.enteredValue = text;
    }

    /**
     * {@return exactly what the user (or CSV file) entered}
     */
    public String getEnteredValue() {
        return enteredValue;
    }
}
