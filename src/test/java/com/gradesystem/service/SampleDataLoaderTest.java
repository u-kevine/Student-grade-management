package com.gradesystem.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleDataLoaderTest {

    @Test
    void loadsFiveStudentsWithFiveGradesEach() throws Exception {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        new SampleDataLoader(students, grades, new SubjectCatalog()).load();

        assertEquals(5, students.getStudentCount());
        assertEquals(25, grades.getTotalGradeCount());
        for (String id : students.getAllStudentIds()) {
            assertEquals(5, grades.getGradesForStudent(id).size());
        }
        assertEquals(2, students.getStudentsOfType("Honors").size());
    }
}
