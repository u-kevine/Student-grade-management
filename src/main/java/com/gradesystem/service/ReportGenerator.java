package com.gradesystem.service;

import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.TextFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Single Responsibility: ONLY builds report TEXT. It never prints and never
 * writes files. The same text is shown on screen (option 4) and saved to a
 * file (option 5), so the report layout lives in one place only.
 */
public class ReportGenerator {

    private static final String NEW_LINE = System.lineSeparator();
    private static final String TITLE_LINE = "=============================================";

    private final GradeManager gradeManager;
    private final GPACalculator gpaCalculator;

    /**
     * Creates a new ReportGenerator.
     *
     * @param gradeManager  where the grades are stored
     * @param gpaCalculator turns grades into GPA points and letters
     */
    public ReportGenerator(GradeManager gradeManager, GPACalculator gpaCalculator) {
        this.gradeManager = gradeManager;
        this.gpaCalculator = gpaCalculator;
    }

    /**
     * Builds a report for one student.
     *
     * @param student the student
     * @param type    which report to build
     * @return the report text
     * @throws NoGradesException if the student has no grades
     */
    public String generateReport(Student student, ReportType type) throws NoGradesException {
        if (!gradeManager.hasGrades(student.getStudentId())) {
            throw new NoGradesException(student.getStudentId());
        }
        StringBuilder report = new StringBuilder();
        addTitle(report, "STUDENT GRADE REPORT - " + type.getLabel().toUpperCase());
        addStudentInfo(report, student);
        if (type == ReportType.DETAILED || type == ReportType.BOTH) {
            addGradeTable(report, student);
        }
        addAverages(report, student);
        addPerformance(report, student);
        return report.toString();
    }

    /**
     * Builds a report listing search results.
     *
     * @param students the students that were found
     * @param criteria a description of the search, for example "Name contains 'john'"
     * @return the report text
     */
    public String generateSearchReport(List<Student> students, String criteria) {
        StringBuilder report = new StringBuilder();
        addTitle(report, "STUDENT SEARCH RESULTS");
        report.append("Search: ").append(criteria).append(NEW_LINE);
        report.append("Found: ").append(students.size()).append(" student(s)").append(NEW_LINE).append(NEW_LINE);
        for (Student student : students) {
            String average = "N/A";
            if (gradeManager.hasGrades(student.getStudentId())) {
                average = TextFormat.percent(gradeManager.calculateAverage(student.getStudentId()));
            }
            // toExportLine() comes from the Exportable interface
            report.append(student.toExportLine()).append(" | Avg: ").append(average).append(NEW_LINE);
        }
        return report.toString();
    }

    private void addTitle(StringBuilder report, String title) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(AppConstants.DATE_PATTERN));
        report.append(TITLE_LINE).append(NEW_LINE);
        report.append(title).append(NEW_LINE);
        report.append("Generated: ").append(today).append(NEW_LINE);
        report.append(TITLE_LINE).append(NEW_LINE).append(NEW_LINE);
    }

    private void addStudentInfo(StringBuilder report, Student student) {
        report.append("Student: ").append(student.getStudentId()).append(" - ").append(student.getName()).append(NEW_LINE);
        report.append("Type: ").append(student.getStudentType()).append(" Student").append(NEW_LINE);
        report.append("Email: ").append(student.getEmail()).append(NEW_LINE);
        report.append("Passing Grade: ").append(TextFormat.number(student.getPassingGrade())).append("%").append(NEW_LINE);
        report.append(NEW_LINE);
    }

    private void addGradeTable(StringBuilder report, Student student) {
        List<Grade> grades = gradeManager.getGradesForStudent(student.getStudentId());
        report.append("GRADE HISTORY (newest first)").append(NEW_LINE);
        report.append(AppConstants.WIDE_LINE).append(NEW_LINE);
        report.append(String.format("%-7s | %-10s | %-18s | %-8s | %-6s | %s%n",
                "GRD ID", "DATE", "SUBJECT", "TYPE", "GRADE", "GPA"));
        report.append(AppConstants.WIDE_LINE).append(NEW_LINE);
        for (int i = grades.size() - 1; i >= 0; i--) {
            Grade g = grades.get(i);
            report.append(String.format("%-7s | %-10s | %-18s | %-8s | %-6s | %.1f (%s)%n",
                    g.getGradeId(), g.getFormattedDate(), g.getSubject().getSubjectName(),
                    g.getSubject().getSubjectType(), TextFormat.number(g.getValue()) + "%",
                    gpaCalculator.toGpaPoints(g.getValue()), gpaCalculator.toLetterGrade(g.getValue())));
        }
        report.append(AppConstants.WIDE_LINE).append(NEW_LINE).append(NEW_LINE);
    }

    private void addAverages(StringBuilder report, Student student) {
        String id = student.getStudentId();
        report.append("Total Grades: ").append(gradeManager.getGradesForStudent(id).size()).append(NEW_LINE);
        report.append("Core Subjects Average: ").append(typeAverage(id, AppConstants.TYPE_CORE)).append(NEW_LINE);
        report.append("Elective Subjects Average: ").append(typeAverage(id, AppConstants.TYPE_ELECTIVE)).append(NEW_LINE);
        report.append("Overall Average: ").append(TextFormat.percent(gradeManager.calculateAverage(id))).append(NEW_LINE);
        double gpa = gpaCalculator.calculate(gradeManager.getGradesForStudent(id));
        report.append("Cumulative GPA: ").append(TextFormat.gpa(gpa)).append(" / ")
                .append(gpaCalculator.getMaxGpa()).append(" (").append(gpaCalculator.gpaToLetterGrade(gpa)).append(")")
                .append(NEW_LINE).append(NEW_LINE);
    }

    private String typeAverage(String studentId, String subjectType) {
        if (gradeManager.getGradesForStudent(studentId, subjectType).isEmpty()) {
            return "N/A";
        }
        return TextFormat.percent(gradeManager.calculateAverage(studentId, subjectType));
    }

    private void addPerformance(StringBuilder report, Student student) {
        double average = gradeManager.calculateAverage(student.getStudentId());
        report.append("PERFORMANCE SUMMARY").append(NEW_LINE);
        if (student.isPassing(average)) {
            report.append("Status: PASSING").append(NEW_LINE);
        } else {
            report.append("Status: FAILING (needs ").append(TextFormat.number(student.getPassingGrade()))
                    .append("% to pass)").append(NEW_LINE);
        }
        if (student.isHonorsEligible(average)) {
            report.append("Honors: Eligible").append(NEW_LINE);
        }
    }
}
