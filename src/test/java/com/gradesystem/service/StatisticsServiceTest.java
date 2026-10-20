package com.gradesystem.service;

import com.gradesystem.calculator.StatisticsCalculator;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.ElectiveSubject;
import com.gradesystem.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    private StatisticsService service;

    @BeforeEach
    void setUp() throws Exception {
        StudentManager students = new StudentManager();
        students.addStudent(TestData.regular("STU001", "Alice"));
        students.addStudent(TestData.honors("STU002", "Bob"));
        students.addStudent(TestData.regular("STU003", "Carol"));   // no grades

        GradeManager grades = new GradeManager();
        Subject math = new CoreSubject("Mathematics", "MTH101");
        Subject music = new ElectiveSubject("Music", "MUS101");
        grades.recordGrade("STU001", math, 42);
        grades.recordGrade("STU001", music, 80);
        grades.recordGrade("STU002", math, 100);
        grades.recordGrade("STU002", music, 80);

        service = new StatisticsService(students, grades, new StatisticsCalculator());
    }

    @Test
    void classWideNumbers() {
        assertEquals(3, service.getTotalStudents());
        assertEquals(4, service.getTotalGrades());
        assertEquals(75.5, service.getMean(), 0.001);
        assertEquals(80.0, service.getMedian(), 0.001);
        assertEquals(OptionalDouble.of(80.0), service.getMode());
        assertEquals(42.0, service.getMin());
        assertEquals(100.0, service.getMax());
        assertEquals(58.0, service.getRange());
        assertEquals(20.99, service.getStandardDeviation(), 0.01);
    }

    @Test
    void distributionHighestAndLowest() {
        assertEquals(1, service.getBandCount("A"));
        assertEquals(2, service.getBandCount("B"));
        assertEquals(1, service.getBandCount("F"));
        assertEquals("STU002", service.getHighestGrade().get().getStudentId());
        assertEquals(42.0, service.getLowestGrade().get().getValue());
    }

    @Test
    void subjectAndStudentTypeAverages() {
        assertEquals(OptionalDouble.of(71.0), service.getSubjectAverage("Mathematics"));
        assertFalse(service.getSubjectAverage("Art").isPresent());
        assertEquals(OptionalDouble.of(80.0), service.getSubjectTypeAverage("Elective"));
        assertEquals(OptionalDouble.of(61.0), service.getStudentTypeAverage("Regular"));   // Carol skipped
        assertEquals(OptionalDouble.of(90.0), service.getStudentTypeAverage("Honors"));
        assertEquals(2, service.countStudentsOfType("Regular"));
        assertEquals(75.5, service.getClassAverage(), 0.001);
    }

    @Test
    void emptyClassGivesEmptyResults() {
        StudentManager emptyStudents = mock(StudentManager.class);
        GradeManager emptyGrades = mock(GradeManager.class);
        when(emptyStudents.getAllStudents()).thenReturn(List.of());
        when(emptyGrades.getAllGrades()).thenReturn(List.of());
        StatisticsService empty = new StatisticsService(emptyStudents, emptyGrades, new StatisticsCalculator());

        assertFalse(empty.getHighestGrade().isPresent());
        assertFalse(empty.getLowestGrade().isPresent());
        assertFalse(empty.getStudentTypeAverage("Honors").isPresent());
        assertEquals(0.0, empty.getClassAverage());
    }
}
