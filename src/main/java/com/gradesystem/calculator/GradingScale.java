package com.gradesystem.calculator;

/**
 * A way of turning percentages into GPA points and letter grades.
 *
 * <p>Strategy pattern + Open/Closed principle: today we use the
 * {@link FourPointGradingScale}. To support another scale (for example a
 * 5.0 scale) you write a new class that implements this interface and pass
 * it to the {@link GPACalculator}. No existing code has to change.</p>
 */
public interface GradingScale {

    /**
     * Converts one percentage to GPA points.
     *
     * @param percentage a grade from 0 to 100
     * @return the GPA points for that grade
     */
    double toGpaPoints(double percentage);

    /**
     * Converts one percentage to a letter grade.
     *
     * @param percentage a grade from 0 to 100
     * @return the letter grade, for example "A-"
     */
    String toLetterGrade(double percentage);

    /**
     * Turns a cumulative GPA (for example 3.74) back into a letter.
     *
     * @param gpa the GPA
     * @return the letter grade, for example "A-"
     */
    String gpaToLetterGrade(double gpa);

    /**
     * {@return the best possible GPA, for example 4.0}
     */
    double getMaxGpa();

}
