package com.gradesystem.ui.screens;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.model.Subject;
import com.gradesystem.service.GradeManager;
import com.gradesystem.service.SubjectCatalog;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentLookup;
import com.gradesystem.util.TextFormat;

import java.util.List;
import java.util.Optional;

/**
 * Menu option 3: Record Grade.
 */
public class RecordGradeScreen implements Screen {

    private final ConsoleIO io;
    private final StudentLookup studentLookup;
    private final GradeManager gradeManager;
    private final SubjectCatalog subjectCatalog;

    /**
     * Creates a new RecordGradeScreen.
     *
     * @param io             console input/output
     * @param studentLookup  asks for the student
     * @param gradeManager   where the grade is stored
     * @param subjectCatalog the subjects to choose from
     */
    public RecordGradeScreen(ConsoleIO io, StudentLookup studentLookup, GradeManager gradeManager,
                             SubjectCatalog subjectCatalog) {
        this.io = io;
        this.studentLookup = studentLookup;
        this.gradeManager = gradeManager;
        this.subjectCatalog = subjectCatalog;
    }

    @Override
    public String getMenuLabel() {
        return "Record Grade";
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void show() {
        io.printTitle("RECORD GRADE");
        Optional<Student> found = studentLookup.askForStudent();
        if (found.isEmpty()) {
            io.pause();
            return;
        }
        Student student = found.get();
        showStudent(student);

        Subject subject = chooseSubject();

        while (true) {
            io.println();
            double value = io.readDouble("Enter grade (0-100): ");
            try {
                Grade grade = gradeManager.recordGrade(student.getStudentId(), subject, value);
                io.println();
                io.showSuccess("Grade recorded successfully!");
                io.println("  Grade ID: " + grade.getGradeId() + " | " + subject.getSubjectName()
                        + " | " + TextFormat.number(value) + "% | " + grade.getFormattedDate());
                break;
            } catch (InvalidGradeException e) {
                io.showError(e);
                if (!io.readYesNo("  Try again? (Y/N): ")) {
                    io.println("Grade cancelled. Nothing was saved.");
                    break;
                }
            }
        }
        io.pause();
    }

    private void showStudent(Student student) {
        String id = student.getStudentId();
        String average = "N/A";
        if (gradeManager.hasGrades(id)) {
            average = TextFormat.percent(gradeManager.calculateAverage(id));
        }
        io.println();
        io.println("Student Details:");
        io.println("  Name: " + student.getName());
        io.println("  Type: " + student.getStudentType() + " Student");
        io.println("  Current Average: " + average);
        io.println();
    }

    /** Asks for the subject type, then the subject. Both lists come from the catalog. */
    private Subject chooseSubject() {
        List<String> types = subjectCatalog.getSubjectTypes();
        io.println("Subject type:");
        for (int i = 0; i < types.size(); i++) {
            io.println((i + 1) + ". " + types.get(i) + " Subject");
        }
        int typeChoice = io.readInt("Select type (1-" + types.size() + "): ", 1, types.size());
        String type = types.get(typeChoice - 1);

        List<Subject> subjects = subjectCatalog.getSubjectsByType(type);
        io.println();
        io.println("Available " + type + " Subjects:");
        for (int i = 0; i < subjects.size(); i++) {
            io.println((i + 1) + ". " + subjects.get(i).getSubjectName());
        }
        int subjectChoice = io.readInt("Select subject (1-" + subjects.size() + "): ", 1, subjects.size());
        return subjects.get(subjectChoice - 1);
    }
}
