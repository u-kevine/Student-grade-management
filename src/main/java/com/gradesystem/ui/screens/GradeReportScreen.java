package com.gradesystem.ui.screens;

import com.gradesystem.exception.NoGradesException;
import com.gradesystem.model.Student;
import com.gradesystem.service.ReportGenerator;
import com.gradesystem.service.ReportType;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentLookup;

import java.util.Optional;

/**
 * Menu option 4: View Grade Report. Shows the same text that option 5 saves to a file.
 */
public class GradeReportScreen implements Screen {

    private final ConsoleIO io;
    private final StudentLookup studentLookup;
    private final ReportGenerator reportGenerator;

    /**
     * Creates a new GradeReportScreen.
     *
     * @param io              console input/output
     * @param studentLookup   asks for the student
     * @param reportGenerator builds the report text
     */
    public GradeReportScreen(ConsoleIO io, StudentLookup studentLookup, ReportGenerator reportGenerator) {
        this.io = io;
        this.studentLookup = studentLookup;
        this.reportGenerator = reportGenerator;
    }

    @Override
    public String getMenuLabel() {
        return "View Grade Report";
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void show() {
        io.printTitle("VIEW GRADE REPORT");
        Optional<Student> student = studentLookup.askForStudent();
        if (student.isPresent()) {
            try {
                io.println();
                io.print(reportGenerator.generateReport(student.get(), ReportType.DETAILED));
            } catch (NoGradesException e) {
                io.showError(e);
            }
        }
        io.pause();
    }
}
