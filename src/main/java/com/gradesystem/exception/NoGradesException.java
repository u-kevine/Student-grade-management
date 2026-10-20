package com.gradesystem.exception;

/**
 * Thrown when an action needs grades (GPA, report export) but the
 * student has none yet.
 */
public class NoGradesException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new NoGradesException.
     *
     * @param studentId the student without grades
     */
    public NoGradesException(String studentId) {
        super("Student " + studentId + " has no grades recorded yet.",
                "Record a grade first (option 3) or import grades (option 7).");
    }
}
