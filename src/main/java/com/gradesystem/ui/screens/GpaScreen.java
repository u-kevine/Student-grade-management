package com.gradesystem.ui.screens;

import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.service.GPAService;
import com.gradesystem.service.GradeManager;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentLookup;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.TextFormat;

import java.util.Optional;

/**
 * Menu option 6: Calculate Student GPA (US-3).
 */
public class GpaScreen implements Screen {

    private final ConsoleIO io;
    private final StudentLookup studentLookup;
    private final GradeManager gradeManager;
    private final GPAService gpaService;

    /**
     * Creates a new GpaScreen.
     *
     * @param io            console input/output
     * @param studentLookup asks for the student
     * @param gradeManager  gives the student's grades
     * @param gpaService    calculates GPA and rank
     */
    public GpaScreen(ConsoleIO io, StudentLookup studentLookup, GradeManager gradeManager, GPAService gpaService) {
        this.io = io;
        this.studentLookup = studentLookup;
        this.gradeManager = gradeManager;
        this.gpaService = gpaService;
    }

    @Override
    public String getMenuLabel() {
        return "Calculate Student GPA";
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void show() {
        io.printTitle("CALCULATE STUDENT GPA");
        Optional<Student> found = studentLookup.askForStudent();
        if (found.isPresent()) {
            try {
                showGpa(found.get());
            } catch (StudentNotFoundException | NoGradesException e) {
                io.showError(e);
            }
        }
        io.pause();
    }

    private void showGpa(Student student) throws StudentNotFoundException, NoGradesException {
        String id = student.getStudentId();
        double gpa = gpaService.calculateGpa(id);   // throws NoGradesException if there are no grades
        double average = gradeManager.calculateAverage(id);
        GPACalculator calculator = gpaService.getGpaCalculator();

        io.println();
        io.println("Student: " + id + " - " + student.getName());
        io.println("Type: " + student.getStudentType() + " Student");
        io.println("Overall Average: " + TextFormat.percent(average));
        io.println();
        io.println("GPA CALCULATION (" + calculator.getMaxGpa() + " Scale)");
        io.println(AppConstants.LINE);
        io.printf("%-18s | %-6s | %s%n", "Subject", "Grade", "GPA Points");
        io.println(AppConstants.LINE);
        for (Grade grade : gradeManager.getGradesForStudent(id)) {
            io.printf("%-18s | %-6s | %.1f (%s)%n",
                    grade.getSubject().getSubjectName(),
                    TextFormat.number(grade.getValue()) + "%",
                    calculator.toGpaPoints(grade.getValue()),
                    calculator.toLetterGrade(grade.getValue()));
        }
        io.println(AppConstants.LINE);
        io.println();
        io.println("Cumulative GPA: " + TextFormat.gpa(gpa) + " / " + calculator.getMaxGpa());
        io.println("Letter Grade: " + calculator.gpaToLetterGrade(gpa));
        io.println("Class Rank: " + gpaService.getClassRank(id) + " of " + gpaService.getRankedStudentCount());
        io.println();
        showAnalysis(student, gpa, average);
    }

    private void showAnalysis(Student student, double gpa, double average) {
        io.println("Performance Analysis:");
        if (gpa >= AppConstants.GPA_EXCELLENT) {
            io.println("✓ Excellent performance (" + AppConstants.GPA_EXCELLENT + "+ GPA)");
        } else if (gpa >= AppConstants.GPA_GOOD) {
            io.println("✓ Good performance (" + AppConstants.GPA_GOOD + "+ GPA)");
        } else if (gpa >= AppConstants.GPA_SATISFACTORY) {
            io.println("• Satisfactory performance (" + AppConstants.GPA_SATISFACTORY + "+ GPA)");
        } else {
            io.println("✗ Needs improvement (below " + AppConstants.GPA_SATISFACTORY + " GPA)");
        }

        if (student.isHonorsEligible(average)) {
            io.println("✓ Honors eligibility maintained");
        } else if (student.getStudentType().equals(AppConstants.TYPE_HONORS)) {
            io.println("✗ Honors eligibility at risk (needs "
                    + TextFormat.number(AppConstants.HONORS_ELIGIBILITY_AVERAGE) + "% average)");
        }

        double classGpa = gpaService.getClassAverageGpa();
        String classText = "class average (" + TextFormat.gpa(classGpa) + " GPA)";
        if (gpa > classGpa) {
            io.println("✓ Above " + classText);
        } else if (gpa < classGpa) {
            io.println("• Below " + classText);
        } else {
            io.println("• Equal to " + classText);
        }
    }
}
