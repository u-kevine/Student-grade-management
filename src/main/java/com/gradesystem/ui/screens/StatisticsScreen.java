package com.gradesystem.ui.screens;

import com.gradesystem.model.Grade;
import com.gradesystem.model.Subject;
import com.gradesystem.service.StatisticsService;
import com.gradesystem.service.SubjectCatalog;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.MathUtils;
import com.gradesystem.util.TextFormat;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Menu option 8: View Class Statistics (US-5).
 */
public class StatisticsScreen implements Screen {

    /** Band letter and the label shown in front of its bar. */
    private static final String[][] BANDS = {
            {"A", "90-100% (A)"}, {"B", "80-89%  (B)"}, {"C", "70-79%  (C)"}, {"D", "60-69%  (D)"}, {"F", "0-59%   (F)"}
    };
    private static final String[] STUDENT_TYPES = {AppConstants.TYPE_REGULAR, AppConstants.TYPE_HONORS};

    private final ConsoleIO io;
    private final StatisticsService statisticsService;
    private final SubjectCatalog subjectCatalog;

    /**
     * Creates a new StatisticsScreen.
     *
     * @param io                console input/output
     * @param statisticsService gives the numbers
     * @param subjectCatalog    the subjects to show averages for
     */
    public StatisticsScreen(ConsoleIO io, StatisticsService statisticsService, SubjectCatalog subjectCatalog) {
        this.io = io;
        this.statisticsService = statisticsService;
        this.subjectCatalog = subjectCatalog;
    }

    @Override
    public String getMenuLabel() {
        return "View Class Statistics";
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void show() {
        io.printTitle("CLASS STATISTICS");
        io.println("Total Students: " + statisticsService.getTotalStudents());
        io.println("Total Grades Recorded: " + statisticsService.getTotalGrades());

        if (statisticsService.getTotalGrades() == 0) {
            io.println();
            io.println("No grades recorded yet. Record or import grades first.");
        } else {
            showDistribution();
            showAnalysis();
            showSubjectPerformance();
            showStudentTypes();
        }
        io.pause();
    }

    private void showDistribution() {
        int total = statisticsService.getTotalGrades();
        io.println();
        io.println("GRADE DISTRIBUTION");
        io.println(AppConstants.LINE);
        for (String[] band : BANDS) {
            int count = statisticsService.getBandCount(band[0]);
            double percent = MathUtils.percentage(count, total);
            String word = "grades";
            if (count == 1) {
                word = "grade";
            }
            io.printf("%s: %s %5.1f%% (%d %s)%n", band[1], TextFormat.bar(percent), percent, count, word);
        }
    }

    private void showAnalysis() {
        io.println();
        io.println("STATISTICAL ANALYSIS");
        io.println(AppConstants.LINE);
        io.printf("%-20s %s%n", "Mean (Average):", TextFormat.percent(statisticsService.getMean()));
        io.printf("%-20s %s%n", "Median:", TextFormat.percent(statisticsService.getMedian()));
        OptionalDouble mode = statisticsService.getMode();
        if (mode.isPresent()) {
            io.printf("%-20s %s%n", "Mode:", TextFormat.percent(mode.getAsDouble()));
        } else {
            io.printf("%-20s %s%n", "Mode:", "None (no grade repeats)");
        }
        io.printf("%-20s %s%n", "Standard Deviation:", TextFormat.percent(statisticsService.getStandardDeviation()));
        io.printf("%-20s %s (%s%% - %s%%)%n", "Range:", TextFormat.percent(statisticsService.getRange()),
                TextFormat.number(statisticsService.getMin()), TextFormat.number(statisticsService.getMax()));
        io.println();
        printExtreme("Highest Grade:", statisticsService.getHighestGrade());
        printExtreme("Lowest Grade: ", statisticsService.getLowestGrade());
    }

    private void printExtreme(String label, Optional<Grade> grade) {
        if (grade.isPresent()) {
            Grade g = grade.get();
            io.printf("%s %4s%% (%s - %s)%n", label, TextFormat.number(g.getValue()),
                    g.getStudentId(), g.getSubject().getSubjectName());
        }
    }

    private void showSubjectPerformance() {
        io.println();
        io.println("SUBJECT PERFORMANCE");
        io.println(AppConstants.LINE);
        for (String type : subjectCatalog.getSubjectTypes()) {
            io.printf("%-20s %s average%n", type + " Subjects:", formatAverage(statisticsService.getSubjectTypeAverage(type)));
            for (Subject subject : subjectCatalog.getSubjectsByType(type)) {
                io.printf("  %-18s %s%n", subject.getSubjectName() + ":",
                        formatAverage(statisticsService.getSubjectAverage(subject.getSubjectName())));
            }
        }
    }

    private void showStudentTypes() {
        io.println();
        io.println("STUDENT TYPE COMPARISON");
        io.println(AppConstants.LINE);
        for (String type : STUDENT_TYPES) {
            io.printf("%-19s %s average (%d students)%n", type + " Students:",
                    formatAverage(statisticsService.getStudentTypeAverage(type)),
                    statisticsService.countStudentsOfType(type));
        }
    }

    private String formatAverage(OptionalDouble average) {
        if (average.isPresent()) {
            return TextFormat.percent(average.getAsDouble());
        }
        return "N/A";
    }
}
