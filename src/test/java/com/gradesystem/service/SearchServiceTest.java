package com.gradesystem.service;

import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * INTEGRATION TEST WITH MOCKING: the managers are mocks.
 */
class SearchServiceTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private SearchService service;
    private final Student alice = TestData.regular("STU001", "Alice Johnson");
    private final Student bob = TestData.honors("STU002", "Bob Johnson");
    private final Student carol = TestData.regular("STU003", "Carol Martinez");

    @BeforeEach
    void setUp() {
        studentManager = mock(StudentManager.class);
        gradeManager = mock(GradeManager.class);
        service = new SearchService(studentManager, gradeManager);
        when(studentManager.getAllStudents()).thenReturn(List.of(alice, bob, carol));

        when(gradeManager.hasGrades(anyString())).thenReturn(true);
        when(gradeManager.hasGrades("STU003")).thenReturn(false);
        when(gradeManager.calculateAverage("STU001")).thenReturn(81.2);
        when(gradeManager.calculateAverage("STU002")).thenReturn(90.0);
    }

    @Test
    void searchByIdIsExact() {
        when(studentManager.findById("STU002")).thenReturn(Optional.of(bob));
        when(studentManager.findById("STU9")).thenReturn(Optional.empty());
        assertEquals(List.of(bob), service.searchById("STU002"));
        assertTrue(service.searchById("STU9").isEmpty());
    }

    @Test
    void searchByNameIsPartialAndCaseInsensitive() throws InvalidInputException {
        assertEquals(List.of(alice, bob), service.searchByName("JOHN"));
        assertTrue(service.searchByName("xyz").isEmpty());
    }

    @Test
    void emptyNameIsRejected() {
        assertThrows(InvalidInputException.class, () -> service.searchByName("  "));
        assertThrows(InvalidInputException.class, () -> service.searchByName(null));
    }

    @Test
    void searchByGradeRangeIncludesEdgesAndSkipsStudentsWithoutGrades() throws InvalidInputException {
        assertEquals(List.of(alice, bob), service.searchByGradeRange(81.2, 90));
        assertEquals(List.of(bob), service.searchByGradeRange(85, 100));
        assertThrows(InvalidInputException.class, () -> service.searchByGradeRange(90, 80));
    }

    @Test
    void searchByTypeAsksTheManager() {
        when(studentManager.getStudentsOfType("Honors")).thenReturn(List.of(bob));
        assertEquals(List.of(bob), service.searchByType("Honors"));
        verify(studentManager).getStudentsOfType("Honors");
    }
}
