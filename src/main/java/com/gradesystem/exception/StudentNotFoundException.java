package com.gradesystem.exception;

import java.util.List;

/**
 * Thrown when no student has the requested ID.
 */
public class StudentNotFoundException extends GradeSystemException {

    /** Version number used when the exception is serialized. */
    private static final long serialVersionUID = 1L;

    /** The ID that was not found. */
    private final String studentId;

    /**
     * Creates a new StudentNotFoundException.
     *
     * @param studentId    the ID that was not found
     * @param availableIds the IDs that do exist, shown to the user as a hint
     */
    public StudentNotFoundException(String studentId, List<String> availableIds) {
        super("Student with ID '" + studentId + "' not found in the system.", buildHint(availableIds));
        this.studentId = studentId;
    }

    private static String buildHint(List<String> availableIds) {
        if (availableIds.isEmpty()) {
            return "No students have been added yet. Use option 1 to add a student.";
        }
        return "Available student IDs: " + String.join(", ", availableIds);
    }

    /**
     * {@return the ID that was not found}
     */
    public String getStudentId() {
        return studentId;
    }
}
