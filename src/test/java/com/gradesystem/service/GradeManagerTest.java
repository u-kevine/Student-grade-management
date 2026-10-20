package com.gradesystem.service;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.ElectiveSubject;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeManagerTest {

    private final Subject math = new CoreSubject("Mathematics", "MTH101");
    private final Subject music = new ElectiveSubject("Music", "MUS101");
    private GradeManager manager;

    @BeforeEach
    void setUp() throws InvalidGradeException {
        manager = new GradeManager();
        manager.recordGrade("STU001", math, 80);
        manager.recordGrade("STU001", music, 90);
        manager.recordGrade("STU002", math, 70);
    }

    @Test
    void recordsGradesWithSequentialIds() throws InvalidGradeException {
        Grade grade = manager.recordGrade("STU002", music, 60);
        assertEquals("GRD004", grade.getGradeId());
        assertEquals(4, manager.getTotalGradeCount());
    }

    @Test
    void invalidGradeIsNotStored() {
        assertThrows(InvalidGradeException.class, () -> manager.recordGrade("STU001", math, 101));
        assertEquals(3, manager.getTotalGradeCount());
    }

    @Test
    void averagesPerStudentAndPerType() {
        assertEquals(85.0, manager.calculateAverage("STU001"));
        assertEquals(80.0, manager.calculateAverage("STU001", "Core"));
        assertEquals(90.0, manager.calculateAverage("STU001", "Elective"));
        assertEquals(0.0, manager.calculateAverage("STU002", "Elective"));   // no electives = 0, no crash
    }

    @Test
    void filtersGrades() {
        assertEquals(2, manager.getGradesForStudent("stu001").size());
        assertEquals(2, manager.getGradesForSubject("mathematics").size());
        assertEquals(1, manager.getGradesForSubjectType("Elective").size());
        assertTrue(manager.hasGrades("STU002"));
        assertFalse(manager.hasGrades("STU003"));
    }

    @Test
    void staticHelpers() {
        List<Grade> grades = manager.getGradesForStudent("STU001");
        assertEquals(List.of(80.0, 90.0), GradeManager.valuesOf(grades));
        assertEquals(85.0, GradeManager.averageOf(grades));
        assertEquals(0.0, GradeManager.averageOf(List.of()));
        assertThrows(UnsupportedOperationException.class, () -> manager.getAllGrades().clear());
    }
}
