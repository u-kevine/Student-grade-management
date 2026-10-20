package com.gradesystem.service;

import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.RegularStudent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentManagerTest {

    private StudentManager manager;

    @BeforeEach
    void setUp() throws InvalidInputException {
        manager = new StudentManager();
        manager.addStudent(TestData.regular(manager.generateNextId(), "Alice Johnson"));
        manager.addStudent(TestData.honors(manager.generateNextId(), "Bob Smith"));
    }

    @Test
    void generatesSequentialIds() {
        assertEquals(List.of("STU001", "STU002"), manager.getAllStudentIds());
        assertEquals("STU003", manager.generateNextId());
        assertEquals(2, manager.getStudentCount());
    }

    @Test
    void findsStudentIgnoringCaseAndSpaces() throws StudentNotFoundException {
        assertEquals("Alice Johnson", manager.findStudent(" stu001 ").getName());
    }

    @Test
    void unknownIdThrowsWithHelpfulHint() {
        StudentNotFoundException e = assertThrows(StudentNotFoundException.class, () -> manager.findStudent("STU999"));
        assertEquals("Available student IDs: STU001, STU002", e.getRecoveryHint());
        assertTrue(manager.findById("STU999").isEmpty());
    }

    @Test
    void rejectsInvalidStudentData() {
        assertThrows(InvalidInputException.class,
                () -> manager.addStudent(new RegularStudent("STU003", "Bad Email", 16, "not-an-email", "0788000000")));
        assertThrows(InvalidInputException.class,
                () -> manager.addStudent(new RegularStudent("STU003", "Too Young", 2, "a@b.com", "0788000000")));
        assertEquals(2, manager.getStudentCount());
    }

    @Test
    void rejectsDuplicateIds() {
        assertThrows(InvalidInputException.class, () -> manager.addStudent(TestData.regular("STU001", "Copy Cat")));
    }

    @Test
    void filtersByTypeAndListIsReadOnly() {
        assertEquals(1, manager.getStudentsOfType("honors").size());
        assertThrows(UnsupportedOperationException.class, () -> manager.getAllStudents().clear());
    }
}
