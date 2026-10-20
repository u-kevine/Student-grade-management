package com.gradesystem.ui.screens;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.Student;
import com.gradesystem.service.ExportResult;
import com.gradesystem.service.ExportService;
import com.gradesystem.service.GradeManager;
import com.gradesystem.service.ReportType;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentLookup;
import com.gradesystem.util.InputValidator;

import java.util.Optional;

/**
 * Menu option 5: Export Grade Report (US-2).
 */
public class ExportReportScreen implements Screen {

    private final ConsoleIO io;
    private final StudentLookup studentLookup;
    private final GradeManager gradeManager;
    private final ExportService exportService;

    /**
     * Creates a new ExportReportScreen.
     *
     * @param io            console input/output
     * @param studentLookup asks for the student
     * @param gradeManager  used to show how many grades the student has
     * @param exportService builds and saves the report
     */
    public ExportReportScreen(ConsoleIO io, StudentLookup studentLookup, GradeManager gradeManager,
                              ExportService exportService) {
        this.io = io;
        this.studentLookup = studentLookup;
        this.gradeManager = gradeManager;
        this.exportService = exportService;
    }

    @Override
    public String getMenuLabel() {
        return "Export Grade Report";
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void show() {
        io.printTitle("EXPORT GRADE REPORT");
        Optional<Student> found = studentLookup.askForStudent();
        if (found.isEmpty()) {
            io.pause();
            return;
        }
        Student student = found.get();
        int gradeCount = gradeManager.getGradesForStudent(student.getStudentId()).size();

        io.println();
        io.println("Student: " + student.getStudentId() + " - " + student.getName());
        io.println("Type: " + student.getStudentType() + " Student");
        io.println("Total Grades: " + gradeCount);
        io.println();
        io.println("Export options:");
        io.println("1. Summary Report (overview only)");
        io.println("2. Detailed Report (all grades)");
        io.println("3. Both");
        int option = io.readInt("Select option (1-3): ", 1, ReportType.values().length);
        ReportType type = ReportType.values()[option - 1];

        io.println();
        String fileName = io.readValidText("Enter filename (without extension): ", InputValidator::validateFileName);

        try {
            ExportResult result = exportService.exportStudentReport(student.getStudentId(), type, fileName);
            io.println();
            io.showSuccess("Report exported successfully!");
            io.println("  File: " + result.getFileName());
            io.println("  Location: " + result.getLocation());
            io.println("  Size: " + result.getFormattedSize());
            io.println("  Contains: " + gradeCount + " grades, averages, performance summary");
        } catch (StudentNotFoundException | NoGradesException | InvalidInputException | FileExportException e) {
            io.showError(e);
        }
        io.pause();
    }
}
