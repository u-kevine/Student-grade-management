package com.gradesystem.service;

/**
 * The kinds of report a teacher can export.
 */
public enum ReportType {

    /** Overview only: averages, GPA and performance. */
    SUMMARY("Summary Report"),
    /** Every grade plus averages and performance. */
    DETAILED("Detailed Report"),
    /** The summary followed by the detailed report. */
    BOTH("Summary + Detailed Report");

    private final String label;

    ReportType(String label) {
        this.label = label;
    }

    /**
     * {@return the name shown to the user, for example "Detailed Report"}
     */
    public String getLabel() {
        return label;
    }
}
