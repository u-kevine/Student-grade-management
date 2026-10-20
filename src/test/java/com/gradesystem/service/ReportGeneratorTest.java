package com.gradesystem.service;

import com.gradesystem.calculator.FourPointGradingScale;
import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.ElectiveSubject;
import com.gradesystem.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportGeneratorTest {

    private GradeManager gradeManager;
    private ReportGenerator generator;
    private final Student alice = TestData.regular("STU001", "Alice Johnson");
    private final Student bob = TestData.honors("STU002", "Bob Smith");

    @BeforeEach
    void setUp() throws InvalidGradeException {
        gradeManager = new GradeManager();
        generator = new ReportGenerator(gradeManager, new GPACalculator(new FourPointGradingScale()));
        gradeManager.recordGrade("STU001", new CoreSubject("Mathematics", "MTH101"), 95);
        gradeManager.recordGrade("STU001", new ElectiveSubject("Music", "MUS101"), 85);
    }

    @Test
    void summaryHasAveragesButNoGradeTable() throws NoGradesException {
        String report = generator.generateReport(alice, ReportType.SUMMARY);
        assertTrue(report.contains("SUMMARY REPORT"));
        assertTrue(report.contains("Overall Average: 90.0%"));
        assertTrue(report.contains("Cumulative GPA: 3.50 / 4.0 (B+)"));
        assertTrue(report.contains("Status: PASSING"));
        assertFalse(report.contains("GRADE HISTORY"));
    }

    @Test
    void detailedAndBothIncludeEveryGrade() throws NoGradesException {
        String detailed = generator.generateReport(alice, ReportType.DETAILED);
        assertTrue(detailed.contains("GRADE HISTORY"));
        assertTrue(detailed.contains("Mathematics"));
        assertTrue(detailed.contains("4.0 (A)"));
        assertTrue(generator.generateReport(alice, ReportType.BOTH).contains("SUMMARY + DETAILED"));
    }

    @Test
    void showsFailingStatusHonorsAndMissingType() throws Exception {
        gradeManager.recordGrade("STU002", new CoreSubject("Mathematics", "MTH101"), 50);
        String failing = generator.generateReport(bob, ReportType.SUMMARY);
        assertTrue(failing.contains("FAILING (needs 60% to pass)"));
        assertTrue(failing.contains("Elective Subjects Average: N/A"));

        gradeManager.recordGrade("STU002", new CoreSubject("English", "ENG101"), 100);
        gradeManager.recordGrade("STU002", new CoreSubject("Science", "SCI101"), 100);
        gradeManager.recordGrade("STU002", new ElectiveSubject("Art", "ART101"), 100);   // average now 87.5%
        assertTrue(generator.generateReport(bob, ReportType.SUMMARY).contains("Honors: Eligible"));
    }

    @Test
    void studentWithoutGradesThrows() {
        assertThrows(NoGradesException.class, () -> generator.generateReport(bob, ReportType.SUMMARY));
    }

    @Test
    void searchReportListsStudentsWithAverages() {
        String report = generator.generateSearchReport(List.of(alice, bob), "Name contains 'o'");
        assertTrue(report.contains("Found: 2 student(s)"));
        assertTrue(report.contains("STU001 | Alice Johnson | Regular | student@school.edu | Avg: 90.0%"));
        assertTrue(report.contains("Avg: N/A"));
    }
}
