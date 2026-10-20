package com.gradesystem.ui.screens;

import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.model.HonorsStudent;
import com.gradesystem.model.RegularStudent;
import com.gradesystem.model.Student;
import com.gradesystem.service.StudentManager;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentDetailsView;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.InputValidator;
import com.gradesystem.util.TextFormat;

/**
 * Menu option 1: Add Student.
 */
public class AddStudentScreen implements Screen {

    private static final int REGULAR_CHOICE = 1;

    private final ConsoleIO io;
    private final StudentManager studentManager;
    private final StudentDetailsView detailsView;

    /**
     * Creates a new AddStudentScreen.
     *
     * @param io             console input/output
     * @param studentManager where the student is stored
     * @param detailsView    prints the new student
     */
    public AddStudentScreen(ConsoleIO io, StudentManager studentManager, StudentDetailsView detailsView) {
        this.io = io;
        this.studentManager = studentManager;
        this.detailsView = detailsView;
    }

    @Override
    public String getMenuLabel() {
        return "Add Student";
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void show() {
        io.printTitle("ADD STUDENT");

        // Each field is checked as soon as it is typed (validation BEFORE use).
        String name = io.readValidText("Enter student name: ", InputValidator::validateName);
        int age = io.readInt("Enter student age: ", AppConstants.MIN_AGE, AppConstants.MAX_AGE);
        String email = io.readValidText("Enter student email: ", InputValidator::validateEmail);
        String phone = io.readValidText("Enter student phone: ", InputValidator::validatePhone);

        io.println();
        io.println("Student type:");
        io.println("1. Regular Student (Passing grade: " + TextFormat.number(AppConstants.REGULAR_PASSING_GRADE) + "%)");
        io.println("2. Honors Student (Passing grade: " + TextFormat.number(AppConstants.HONORS_PASSING_GRADE)
                + "%, honors recognition)");
        int type = io.readInt("Select type (1-2): ", 1, 2);

        // The variable is a Student; the object is Regular OR Honors (polymorphism).
        String id = studentManager.generateNextId();
        Student student;
        if (type == REGULAR_CHOICE) {
            student = new RegularStudent(id, name, age, email, phone);
        } else {
            student = new HonorsStudent(id, name, age, email, phone);
        }

        try {
            studentManager.addStudent(student);
            io.println();
            io.showSuccess("Student added successfully!");
            detailsView.show(student);
        } catch (InvalidInputException e) {
            io.showError(e);
        }
        io.pause();
    }
}
