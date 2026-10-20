package com.gradesystem.ui;

import com.gradesystem.exception.InputClosedException;
import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.util.InputValidator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleIOTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    /** Creates a ConsoleIO that "types" the given lines and records everything printed. */
    private ConsoleIO consoleWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        return new ConsoleIO(new Scanner(input), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void readIntKeepsAskingUntilValid() {
        ConsoleIO io = consoleWithInput("abc", "99", "3");
        assertEquals(3, io.readInt("Choice: ", 1, 5));
        assertTrue(printed().contains("Please enter a whole number from 1 to 5."));
    }

    @Test
    void readTextRejectsEmptyInput() {
        ConsoleIO io = consoleWithInput("", "Alice");
        assertEquals("Alice", io.readText("Name: "));
        assertTrue(printed().contains("This cannot be empty."));
    }

    @Test
    void readDoubleRejectsText() {
        ConsoleIO io = consoleWithInput("ninety", "92.5");
        assertEquals(92.5, io.readDouble("Grade: "));
        assertTrue(printed().contains("Please enter a number"));
    }

    @Test
    void readYesNoAcceptsAnyCase() {
        ConsoleIO io = consoleWithInput("maybe", "y", "N");
        assertTrue(io.readYesNo("? "));
        assertFalse(io.readYesNo("? "));
        assertTrue(printed().contains("Please type Y or N."));
    }

    @Test
    void readValidTextShowsTheErrorAndAsksAgain() {
        ConsoleIO io = consoleWithInput("bad email", "ok@school.edu");
        assertEquals("ok@school.edu", io.readValidText("Email: ", InputValidator::validateEmail));
        assertTrue(printed().contains("✗ ERROR: InvalidInputException"));
    }

    @Test
    void showErrorPrintsTypeMessageAndHint() {
        ConsoleIO io = consoleWithInput();
        io.showError(new InvalidGradeException(150));
        String text = printed();
        assertTrue(text.contains("✗ ERROR: InvalidGradeException"));
        assertTrue(text.contains("Grade must be between 0 and 100."));
        assertTrue(text.contains("You entered: 150"));
    }

    @Test
    void endOfInputThrowsInputClosedException() {
        ConsoleIO io = new ConsoleIO(new Scanner(""), new PrintStream(output));
        assertThrows(InputClosedException.class, () -> io.readLine("> "));
    }
}
