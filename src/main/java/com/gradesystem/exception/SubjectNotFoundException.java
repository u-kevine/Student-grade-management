package com.gradesystem.exception;

import java.util.List;

/**
 * Thrown when a subject name/type combination does not exist,
 * for example "Chemistry" or "Music" marked as Core.
 */
public class SubjectNotFoundException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SubjectNotFoundException.
     *
     * @param subjectName       the subject that was asked for
     * @param subjectType       the type that was asked for (Core/Elective)
     * @param availableSubjects the subjects that do exist
     */
    public SubjectNotFoundException(String subjectName, String subjectType, List<String> availableSubjects) {
        super("Subject '" + subjectName + "' (" + subjectType + ") does not exist.",
                "Available subjects: " + String.join(", ", availableSubjects));
    }
}
