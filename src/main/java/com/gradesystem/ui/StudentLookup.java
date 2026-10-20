package com.gradesystem.ui;

import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.Student;
import com.gradesystem.service.StudentManager;

import java.util.Optional;

/**
 * Asks for a student ID and keeps offering "Try again?" until a student is
 * found or the user gives up. Used by every screen that needs a student,
 * so this loop is written only once.
 */
public class StudentLookup {

    private final ConsoleIO io;
    private final StudentManager studentManager;

    /**
     * Creates a new StudentLookup.
     *
     * @param io             console input/output
     * @param studentManager where students are looked up
     */
    public StudentLookup(ConsoleIO io, StudentManager studentManager) {
        this.io = io;
        this.studentManager = studentManager;
    }

    /**
     * {@return the student, or empty if the user chose not to try again}
     */
    public Optional<Student> askForStudent() {
        while (true) {
            String id = io.readText("Enter Student ID: ");
            try {
                return Optional.of(studentManager.findStudent(id));
            } catch (StudentNotFoundException e) {
                io.showError(e);
                if (!io.readYesNo("  Try again? (Y/N): ")) {
                    return Optional.empty();
                }
                io.println();
            }
        }
    }
}
