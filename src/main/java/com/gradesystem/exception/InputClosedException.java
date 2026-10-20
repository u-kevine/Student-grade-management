package com.gradesystem.exception;

/**
 * Thrown when the console input stream ends (for example the user presses
 * Ctrl+D, or a test runs out of scripted input).
 *
 * <p>This is the only <i>unchecked</i> exception (it extends
 * {@link RuntimeException}): it can happen on ANY read, and there is nothing
 * to recover, the menu simply closes politely instead of crashing.</p>
 */
public class InputClosedException extends RuntimeException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with a fixed message.
     */
    public InputClosedException() {
        super("Console input was closed.");
    }
}
