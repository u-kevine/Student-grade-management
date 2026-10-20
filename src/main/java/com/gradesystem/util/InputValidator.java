package com.gradesystem.util;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.InvalidInputException;

/**
 * Checks user input BEFORE it is used, and throws a clear custom exception
 * when something is wrong. Checking early prevents most errors deeper in the app.
 */
public final class InputValidator {

    /** Letters (any language), spaces, apostrophes, dots and hyphens. */
    private static final String NAME_PATTERN = "[\\p{L} .'-]+";
    /** Something like name@school.edu */
    private static final String EMAIL_PATTERN = "[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+";
    /** Digits, spaces, +, -, ( and ) only. */
    private static final String PHONE_PATTERN = "[0-9 +()-]{7,20}";
    /** Letters, digits, underscores and hyphens only (safe on every OS). */
    private static final String FILE_NAME_PATTERN = "[A-Za-z0-9_-]+";

    private InputValidator() {
        // utility class: do not instantiate
    }

    /**
     * Checks a student name.
     *
     * @param name the name typed by the user
     * @throws InvalidInputException if it is empty, too long or has odd characters
     */
    public static void validateName(String name) throws InvalidInputException {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("Name cannot be empty.", "Type the student's full name.");
        }
        if (name.length() > AppConstants.MAX_NAME_LENGTH) {
            throw new InvalidInputException("Name is too long.",
                    "Use at most " + AppConstants.MAX_NAME_LENGTH + " characters.");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new InvalidInputException("Name can only contain letters, spaces, ' . and -.",
                    "Example: Alice Johnson");
        }
    }

    /**
     * Checks a student age.
     *
     * @param age the age
     * @throws InvalidInputException if the age is outside the allowed range
     */
    public static void validateAge(int age) throws InvalidInputException {
        if (age < AppConstants.MIN_AGE || age > AppConstants.MAX_AGE) {
            throw new InvalidInputException("Age must be between " + AppConstants.MIN_AGE
                    + " and " + AppConstants.MAX_AGE + ".", "You entered: " + age);
        }
    }

    /**
     * Checks an email address.
     *
     * @param email the email
     * @throws InvalidInputException if it does not look like an email
     */
    public static void validateEmail(String email) throws InvalidInputException {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new InvalidInputException("'" + email + "' is not a valid email.",
                    "Example: name@school.edu");
        }
    }

    /**
     * Checks a phone number.
     *
     * @param phone the phone number
     * @throws InvalidInputException if it has letters or is too short/long
     */
    public static void validatePhone(String phone) throws InvalidInputException {
        if (phone == null || !phone.matches(PHONE_PATTERN)) {
            throw new InvalidInputException("'" + phone + "' is not a valid phone number.",
                    "Use 7-20 digits, for example +250 788 123 456");
        }
    }

    /**
     * Checks that a grade is between 0 and 100.
     *
     * @param grade the grade
     * @throws InvalidGradeException if it is out of range
     */
    public static void validateGrade(double grade) throws InvalidGradeException {
        if (Double.isNaN(grade) || grade < AppConstants.MIN_GRADE || grade > AppConstants.MAX_GRADE) {
            throw new InvalidGradeException(grade);
        }
    }

    /**
     * Turns text such as "85" into a grade and checks its range.
     *
     * @param text the text to convert
     * @return the grade as a number
     * @throws InvalidGradeException if the text is not a number or is out of range
     */
    public static double parseGrade(String text) throws InvalidGradeException {
        double grade;
        try {
            grade = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidGradeException(text);
        }
        validateGrade(grade);
        return grade;
    }

    /**
     * Checks a file name typed by the user (without extension).
     *
     * @param fileName the file name
     * @throws InvalidInputException if it contains characters that are unsafe in file names
     */
    public static void validateFileName(String fileName) throws InvalidInputException {
        if (fileName == null || !fileName.matches(FILE_NAME_PATTERN)) {
            throw new InvalidInputException("'" + fileName + "' is not a valid file name.",
                    "Use only letters, numbers, _ and - (example: alice_report).");
        }
    }

    /**
     * Checks a grade range used for searching, for example 80 to 90.
     *
     * @param min the lowest grade in the range
     * @param max the highest grade in the range
     * @throws InvalidInputException if the numbers are out of range or in the wrong order
     */
    public static void validateGradeRange(double min, double max) throws InvalidInputException {
        if (min < AppConstants.MIN_GRADE || max > AppConstants.MAX_GRADE) {
            throw new InvalidInputException("Range must stay between 0 and 100.",
                    "You entered: " + TextFormat.number(min) + " - " + TextFormat.number(max));
        }
        if (min > max) {
            throw new InvalidInputException("The minimum cannot be bigger than the maximum.",
                    "Example: 80 to 90");
        }
    }
}
