package com.gradesystem.exception;

/**
 * Parent class of every custom exception in this application.
 *
 * <p>Besides the normal error message, each exception carries a
 * <b>recovery hint</b>: a short tip that tells the user how to fix the problem
 * (for example "Available student IDs: STU001, STU002").</p>
 *
 * <p>It is a <i>checked</i> exception (it extends {@link Exception}), so the
 * compiler forces us to handle it. That way no error can be forgotten.</p>
 */
public class GradeSystemException extends Exception {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /** Tip that tells the user how to fix the problem. */
    private final String recoveryHint;

    /**
     * Creates an exception with a message and a hint.
     *
     * @param message      what went wrong
     * @param recoveryHint how the user can fix it
     */
    public GradeSystemException(String message, String recoveryHint) {
        super(message);
        this.recoveryHint = recoveryHint;
    }

    /**
     * Creates an exception that wraps a lower-level cause (for example an IOException).
     *
     * @param message      what went wrong
     * @param recoveryHint how the user can fix it
     * @param cause        the original exception
     */
    public GradeSystemException(String message, String recoveryHint, Throwable cause) {
        super(message, cause);
        this.recoveryHint = recoveryHint;
    }

    /**
     * Returns the tip that tells the user how to recover.
     *
     * @return the recovery hint
     */
    public String getRecoveryHint() {
        return recoveryHint;
    }
}
