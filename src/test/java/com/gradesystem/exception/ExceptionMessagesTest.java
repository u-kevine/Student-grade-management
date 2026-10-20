package com.gradesystem.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every custom exception must give a clear message AND a useful recovery hint.
 */
class ExceptionMessagesTest {

    @Test
    void studentNotFoundListsAvailableIds() {
        StudentNotFoundException e = new StudentNotFoundException("STU999", List.of("STU001", "STU002"));
        assertEquals("Student with ID 'STU999' not found in the system.", e.getMessage());
        assertEquals("Available student IDs: STU001, STU002", e.getRecoveryHint());
        assertEquals("STU999", e.getStudentId());
        assertInstanceOf(GradeSystemException.class, e);
    }

    @Test
    void studentNotFoundWithNoStudentsSuggestsAddingOne() {
        StudentNotFoundException e = new StudentNotFoundException("STU001", List.of());
        assertTrue(e.getRecoveryHint().contains("option 1"));
    }

    @Test
    void invalidGradeShowsWhatWasEntered() {
        InvalidGradeException number = new InvalidGradeException(150.0);
        assertEquals("Grade must be between 0 and 100.", number.getMessage());
        assertEquals("You entered: 150", number.getRecoveryHint());
        assertEquals("150", number.getEnteredValue());

        InvalidGradeException text = new InvalidGradeException("abc");
        assertEquals("You entered: abc", text.getRecoveryHint());
    }

    @Test
    void fileFormatExplainsTheExpectedHeader() {
        InvalidFileFormatException e = new InvalidFileFormatException("bad.csv", "the file is empty.");
        assertTrue(e.getMessage().contains("bad.csv"));
        assertTrue(e.getRecoveryHint().contains("StudentID,SubjectName,SubjectType,Grade"));
    }

    @Test
    void subjectNotFoundListsSubjects() {
        SubjectNotFoundException e = new SubjectNotFoundException("Chemistry", "Core", List.of("Mathematics (Core)"));
        assertTrue(e.getMessage().contains("Chemistry"));
        assertEquals("Available subjects: Mathematics (Core)", e.getRecoveryHint());
    }

    @Test
    void fileExceptionsKeepTheOriginalCause() {
        IOException cause = new IOException("disk full");
        FileExportException export = new FileExportException("report.txt", cause);
        assertSame(cause, export.getCause());
        assertTrue(export.getMessage().contains("report.txt"));

        FileImportException readError = new FileImportException("grades.csv", cause);
        assertSame(cause, readError.getCause());

        FileImportException missing = new FileImportException("grades.csv");
        assertTrue(missing.getMessage().contains("not found"));
        assertTrue(missing.getRecoveryHint().contains("imports"));
    }

    @Test
    void otherExceptions() {
        assertTrue(new NoGradesException("STU001").getMessage().contains("STU001"));
        assertEquals("hint", new InvalidInputException("msg", "hint").getRecoveryHint());
        assertEquals("Console input was closed.", new InputClosedException().getMessage());
    }
}
