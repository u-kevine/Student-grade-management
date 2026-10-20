package com.gradesystem.ui;

import com.gradesystem.model.Student;
import com.gradesystem.service.GradeManager;
import com.gradesystem.util.TextFormat;

/**
 * Prints all details of one student. Used after adding a student and when
 * viewing a search result, so the layout is defined once.
 */
public class StudentDetailsView {

    private final ConsoleIO io;
    private final GradeManager gradeManager;

    /**
     * Creates a new StudentDetailsView.
     *
     * @param io           console input/output
     * @param gradeManager used to show the student's average
     */
    public StudentDetailsView(ConsoleIO io, GradeManager gradeManager) {
        this.io = io;
        this.gradeManager = gradeManager;
    }

    /**
     * Prints all details of one student.
     *
     * @param student the student to show
     */
    public void show(Student student) {
        String id = student.getStudentId();
        io.println("  Student ID: " + id);
        io.println("  Name: " + student.getName());
        io.println("  Type: " + student.getStudentType() + " Student");
        io.println("  Age: " + student.getAge());
        io.println("  Email: " + student.getEmail());
        io.println("  Phone: " + student.getPhone());
        io.println("  Passing Grade: " + TextFormat.number(student.getPassingGrade()) + "%");
        io.println("  Status: " + student.getStatus());
        if (gradeManager.hasGrades(id)) {
            double average = gradeManager.calculateAverage(id);
            io.println("  Grades Recorded: " + gradeManager.getGradesForStudent(id).size());
            io.println("  Current Average: " + TextFormat.percent(average));
            if (student.isHonorsEligible(average)) {
                io.println("  Honors Eligible: Yes");
            }
        } else {
            io.println("  Grades Recorded: 0");
        }
    }
}
