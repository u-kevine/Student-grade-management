package com.gradesystem.ui;

import com.gradesystem.exception.GradeSystemException;
import com.gradesystem.exception.InputClosedException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * All reading from and writing to the console goes through this class.
 *
 * <p>The Scanner and PrintStream are passed in (dependency injection). The
 * real app uses the keyboard and screen; tests pass in scripted text and
 * capture the output, so the whole menu can be tested automatically.</p>
 */
public class ConsoleIO {

    private static final Logger LOGGER = AppLogger.getLogger(ConsoleIO.class);

    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Creates a new ConsoleIO.
     *
     * @param scanner where input comes from
     * @param out     where output goes to
     */
    public ConsoleIO(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    // ---------------- Output ----------------

    /**
     * Prints text followed by a new line.
     *
     * @param text the text to print on its own line
     */
    public void println(String text) {
        out.println(text);
    }

    /** Prints an empty line. */
    public void println() {
        out.println();
    }

    /**
     * Prints text without a new line.
     *
     * @param text the text to print without a new line
     */
    public void print(String text) {
        out.print(text);
    }

    /**
     * Prints formatted text, like {@code System.out.printf}.
     *
     * @param format the format, for example "%-8s | %s%n"
     * @param args   the values
     */
    public void printf(String format, Object... args) {
        out.printf(format, args);
    }

    /**
     * Prints a screen title with a line under it.
     *
     * @param title for example "RECORD GRADE"
     */
    public void printTitle(String title) {
        out.println(title);
        out.println(AppConstants.LINE);
        out.println();
    }

    /**
     * Prints a green-tick message.
     *
     * @param message for example "Grade recorded successfully!"
     */
    public void showSuccess(String message) {
        out.println("✓ " + message);
    }

    /**
     * Shows a custom exception in a friendly way AND writes it to the log file.
     *
     * <pre>
     * ✗ ERROR: InvalidGradeException
     *   Grade must be between 0 and 100.
     *
     *   You entered: 150
     * </pre>
     *
     * @param error the exception that was caught
     */
    public void showError(GradeSystemException error) {
        LOGGER.warning(error.getClass().getSimpleName() + ": " + error.getMessage());
        out.println();
        out.println("✗ ERROR: " + error.getClass().getSimpleName());
        out.println("  " + error.getMessage());
        out.println();
        out.println("  " + error.getRecoveryHint());
        out.println();
    }

    // ---------------- Input ----------------

    /**
     * Reads one line exactly as typed (can be empty).
     *
     * @param prompt the question to show
     * @return the line, trimmed
     * @throws InputClosedException if there is no more input
     */
    public String readLine(String prompt) {
        out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    /**
     * Reads a line that is not empty, asking again until it is.
     *
     * @param prompt the question to show
     * @return the text
     */
    public String readText(String prompt) {
        String text = readLine(prompt);
        while (text.isEmpty()) {
            out.println("This cannot be empty. Try again.");
            text = readLine(prompt);
        }
        return text;
    }

    /**
     * Reads text and keeps asking until it passes the given rule.
     *
     * @param prompt the question to show
     * @param rule   the rule, for example {@code InputValidator::validateEmail}
     * @return valid text
     */
    public String readValidText(String prompt, InputCheck rule) {
        while (true) {
            String text = readText(prompt);
            try {
                rule.check(text);
                return text;
            } catch (InvalidInputException e) {
                showError(e);
            }
        }
    }

    /**
     * Reads a whole number between min and max, asking again until it is valid.
     *
     * @param prompt the question to show
     * @param min    lowest allowed number
     * @param max    highest allowed number
     * @return the number
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readText(prompt);
            try {
                int number = Integer.parseInt(text);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException e) {
                // not a whole number: fall through to the message below
            }
            out.println("Please enter a whole number from " + min + " to " + max + ".");
        }
    }

    /**
     * Reads any decimal number, asking again until it is a number.
     * The RANGE is checked by the caller, so the right custom exception is shown.
     *
     * @param prompt the question to show
     * @return the number
     */
    public double readDouble(String prompt) {
        while (true) {
            String text = readText(prompt);
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                out.println("Please enter a number, for example 75.");
            }
        }
    }

    /**
     * Reads Y or N (any case).
     *
     * @param prompt the question to show
     * @return true for Y, false for N
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            String answer = readText(prompt);
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("N")) {
                return false;
            }
            out.println("Please type Y or N.");
        }
    }

    /** Waits until the user presses Enter. */
    public void pause() {
        out.println();
        readLine("Press Enter to continue...");
    }
}
