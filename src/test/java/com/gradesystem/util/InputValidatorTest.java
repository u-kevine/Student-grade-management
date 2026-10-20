package com.gradesystem.util;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"Alice Johnson", "Jean-Luc O'Neil", "Émile Dupont", "Dr. Ann Lee"})
    void acceptsValidNames(String name) {
        assertDoesNotThrow(() -> InputValidator.validateName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "R2D2", "alice@home"})
    void rejectsInvalidNames(String name) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateName(name));
    }

    @Test
    void rejectsNullAndTooLongNames() {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateName(null));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateName("A".repeat(51)));
    }

    @Test
    void ageMustBeInRange() {
        assertDoesNotThrow(() -> InputValidator.validateAge(5));
        assertDoesNotThrow(() -> InputValidator.validateAge(100));
        InvalidInputException e = assertThrows(InvalidInputException.class, () -> InputValidator.validateAge(4));
        assertEquals("You entered: 4", e.getRecoveryHint());
        assertThrows(InvalidInputException.class, () -> InputValidator.validateAge(101));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name@school.edu", "a.b+c@mail.co.uk"})
    void acceptsValidEmails(String email) {
        assertDoesNotThrow(() -> InputValidator.validateEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"name", "name@school", "@school.edu", "na me@school.edu"})
    void rejectsInvalidEmails(String email) {
        assertThrows(InvalidInputException.class, () -> InputValidator.validateEmail(email));
    }

    @Test
    void phoneValidation() {
        assertDoesNotThrow(() -> InputValidator.validatePhone("+250 788 123 456"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validatePhone("12345"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validatePhone("call me"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validatePhone(null));
    }

    @Test
    void gradeMustBeBetweenZeroAndHundred() {
        assertDoesNotThrow(() -> InputValidator.validateGrade(0));
        assertDoesNotThrow(() -> InputValidator.validateGrade(100));
        assertThrows(InvalidGradeException.class, () -> InputValidator.validateGrade(-0.1));
        assertThrows(InvalidGradeException.class, () -> InputValidator.validateGrade(100.1));
        assertThrows(InvalidGradeException.class, () -> InputValidator.validateGrade(Double.NaN));
    }

    @Test
    void parseGradeHandlesTextNumbersAndBadInput() throws InvalidGradeException {
        assertEquals(85.5, InputValidator.parseGrade(" 85.5 "));
        InvalidGradeException notNumber = assertThrows(InvalidGradeException.class,
                () -> InputValidator.parseGrade("abc"));
        assertEquals("abc", notNumber.getEnteredValue());
        InvalidGradeException outOfRange = assertThrows(InvalidGradeException.class,
                () -> InputValidator.parseGrade("105"));
        assertEquals("105", outOfRange.getEnteredValue());
    }

    @Test
    void fileNameValidation() {
        assertDoesNotThrow(() -> InputValidator.validateFileName("alice_report-2"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateFileName("../secret"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateFileName("my report"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateFileName(null));
    }

    @Test
    void gradeRangeValidation() {
        assertDoesNotThrow(() -> InputValidator.validateGradeRange(80, 90));
        InvalidInputException outside = assertThrows(InvalidInputException.class,
                () -> InputValidator.validateGradeRange(-5, 90));
        assertTrue(outside.getRecoveryHint().contains("-5"));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateGradeRange(80, 101));
        assertThrows(InvalidInputException.class, () -> InputValidator.validateGradeRange(90, 80));
    }
}
