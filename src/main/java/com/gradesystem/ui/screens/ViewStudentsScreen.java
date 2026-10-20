package com.gradesystem.ui.screens;

import com.gradesystem.model.Student;
import com.gradesystem.service.GradeManager;
import com.gradesystem.service.StatisticsService;
import com.gradesystem.service.StudentManager;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.TextFormat;

/**
 * Menu option 2: View Students.
 */
public class ViewStudentsScreen implements Screen {

    private static final String ROW_FORMAT = "%-8s | %-17s | %-8s | %-9s | %s%n";

    private final ConsoleIO io;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final StatisticsService statisticsService;

    /**
     * Creates a new ViewStudentsScreen.
     *
     * @param io                console input/output
     * @param studentManager    where the students are stored
     * @param gradeManager      used for each student's average
     * @param statisticsService used for the class average
     */
    public ViewStudentsScreen(ConsoleIO io, StudentManager studentManager, GradeManager gradeManager,
                              StatisticsService statisticsService) {
        this.io = io;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.statisticsService = statisticsService;
    }

    @Override
    public String getMenuLabel() {
        return "View Students";
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void show() {
        io.printTitle("STUDENT LISTING");
        if (studentManager.getStudentCount() == 0) {
            io.println("No students yet. Use option 1 to add a student.");
            io.pause();
            return;
        }
        io.println(AppConstants.WIDE_LINE);
        io.printf(ROW_FORMAT, "STU ID", "NAME", "TYPE", "AVG GRADE", "STATUS");
        io.println(AppConstants.WIDE_LINE);

        for (Student student : studentManager.getAllStudents()) {
            printStudentRow(student);
        }

        io.println();
        io.println("Total Students: " + studentManager.getStudentCount());
        io.println("Average Class Grade: " + TextFormat.percent(statisticsService.getClassAverage()));
        io.pause();
    }

    private void printStudentRow(Student student) {
        String id = student.getStudentId();
        String average = "N/A";
        String status = "No Grades";
        String honors = "";
        if (gradeManager.hasGrades(id)) {
            double value = gradeManager.calculateAverage(id);
            average = TextFormat.percent(value);
            if (student.isPassing(value)) {
                status = "Passing";
            } else {
                status = "Failing";
            }
            if (student.isHonorsEligible(value)) {
                honors = " | Honors Eligible";
            }
        }
        io.printf(ROW_FORMAT, id, student.getName(), student.getStudentType(), average, status);
        io.println("         | Grades Recorded: " + gradeManager.getGradesForStudent(id).size()
                + " | Passing Grade: " + TextFormat.number(student.getPassingGrade()) + "%" + honors);
        io.println(AppConstants.WIDE_LINE);
    }
}
