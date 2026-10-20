package com.gradesystem.service;

import com.gradesystem.calculator.FourPointGradingScale;
import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * INTEGRATION TEST WITH MOCKING: both managers are mocks, so we decide exactly
 * which students and grades exist, and test only the GPA/rank logic.
 */
class GPAServiceTest {

    private final Subject math = new CoreSubject("Mathematics", "MTH101");
    private StudentManager studentManager;
    private GradeManager gradeManager;
    private GPAService service;
    private final Student alice = TestData.regular("STU001", "Alice");
    private final Student bob = TestData.honors("STU002", "Bob");
    private final Student carol = TestData.regular("STU003", "Carol");
    private final Student dan = TestData.regular("STU004", "Dan");

    private List<Grade> grades(double... values) throws InvalidGradeException {
        Grade[] result = new Grade[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = new Grade("GRD" + i, "X", math, values[i], LocalDate.now());
        }
        return List.of(result);
    }

    @BeforeEach
    void setUp() throws Exception {
        studentManager = mock(StudentManager.class);
        gradeManager = mock(GradeManager.class);
        service = new GPAService(studentManager, gradeManager, new GPACalculator(new FourPointGradingScale()));

        when(studentManager.getAllStudents()).thenReturn(List.of(alice, bob, carol, dan));
        when(studentManager.findStudent("STU001")).thenReturn(alice);
        when(studentManager.findStudent("STU002")).thenReturn(bob);
        when(studentManager.findStudent("STU003")).thenReturn(carol);
        when(studentManager.findStudent("STU004")).thenReturn(dan);
        when(studentManager.findStudent("STU999")).thenThrow(new StudentNotFoundException("STU999", List.of()));

        when(gradeManager.getGradesForStudent(anyString())).thenReturn(List.of());
        when(gradeManager.getGradesForStudent("STU001")).thenReturn(grades(95, 85));   // GPA 3.5
        when(gradeManager.getGradesForStudent("STU002")).thenReturn(grades(95, 95));   // GPA 4.0
        when(gradeManager.getGradesForStudent("STU003")).thenReturn(grades(85, 95));   // GPA 3.5 (tie)
    }

    @Test
    void calculatesCumulativeGpa() throws Exception {
        assertEquals(3.5, service.calculateGpa("STU001"), 0.001);
        assertEquals(4.0, service.calculateGpa("STU002"), 0.001);
    }

    @Test
    void ranksByGpaAndTiesShareARank() throws Exception {
        assertEquals(1, service.getClassRank("STU002"));
        assertEquals(2, service.getClassRank("STU001"));
        assertEquals(2, service.getClassRank("STU003"));
        assertEquals(3, service.getRankedStudentCount());   // Dan has no grades and is not ranked
    }

    @Test
    void classAverageGpaSkipsStudentsWithoutGrades() {
        assertEquals((3.5 + 4.0 + 3.5) / 3, service.getClassAverageGpa(), 0.001);
    }

    @Test
    void errorsForUnknownStudentOrNoGrades() {
        assertThrows(StudentNotFoundException.class, () -> service.calculateGpa("STU999"));
        assertThrows(NoGradesException.class, () -> service.getClassRank("STU004"));
    }

    @Test
    void emptyClassHasZeroAverage() {
        when(studentManager.getAllStudents()).thenReturn(List.of());
        assertEquals(0.0, service.getClassAverageGpa());
        assertSame(service.getGpaCalculator(), service.getGpaCalculator());
    }
}
