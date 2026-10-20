package com.gradesystem.util;

/**
 * Helpers that turn numbers into nicely formatted text.
 * Keeping them in one place means every screen and report looks the same.
 */
public final class TextFormat {

    private TextFormat() {
        // utility class: do not instantiate
    }

    /**
     * Formats a percentage with one decimal, for example {@code 81.2%}.
     *
     * @param value the percentage
     * @return the formatted text
     */
    public static String percent(double value) {
        return String.format("%.1f%%", value);
    }

    /**
     * Formats a grade without ".0" when it is a whole number:
     * 150.0 becomes "150", 72.5 stays "72.5".
     *
     * @param value the grade
     * @return the formatted text
     */
    public static String number(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    /**
     * Formats a GPA with two decimals, for example {@code 3.74}.
     *
     * @param gpa the GPA
     * @return the formatted text
     */
    public static String gpa(double gpa) {
        return String.format("%.2f", gpa);
    }

    /**
     * Builds a text bar like {@code ████░░░░} for the distribution chart.
     *
     * @param percentage how full the bar is (0 to 100)
     * @return a bar that is always {@link AppConstants#BAR_LENGTH} characters long
     */
    public static String bar(double percentage) {
        int filled = (int) Math.round(percentage / 100.0 * AppConstants.BAR_LENGTH);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < AppConstants.BAR_LENGTH; i++) {
            if (i < filled) {
                bar.append('█');
            } else {
                bar.append('░');
            }
        }
        return bar.toString();
    }
}
