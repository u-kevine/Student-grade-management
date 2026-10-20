package com.gradesystem.calculator;

import com.gradesystem.model.Grade;
import com.gradesystem.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Single Responsibility: this class ONLY does GPA math.
 *
 * <p>Dependency Inversion: it does not know which scale is used. The scale is
 * passed in through the constructor ("dependency injection").</p>
 */
public class GPACalculator implements Calculable {

    private final GradingScale gradingScale;

    /**
     * Creates a new GPACalculator.
     *
     * @param gradingScale the scale to use, for example {@link FourPointGradingScale}
     */
    public GPACalculator(GradingScale gradingScale) {
        this.gradingScale = gradingScale;
    }

    /**
     * Cumulative GPA = the average of the GPA points of every grade.
     *
     * @param grades the student's grades
     * @return the cumulative GPA, or 0 when there are no grades
     */
    @Override
    public double calculate(List<Grade> grades) {
        List<Double> points = new ArrayList<>();
        for (Grade grade : grades) {
            points.add(gradingScale.toGpaPoints(grade.getValue()));
        }
        return MathUtils.average(points);
    }

    /**
     * Converts one percentage to GPA points.
     *
     * @param percentage a grade from 0 to 100
     * @return its GPA points
     */
    public double toGpaPoints(double percentage) {
        return gradingScale.toGpaPoints(percentage);
    }

    /**
     * Converts one percentage to a letter grade.
     *
     * @param percentage a grade from 0 to 100
     * @return its letter grade
     */
    public String toLetterGrade(double percentage) {
        return gradingScale.toLetterGrade(percentage);
    }

    /**
     * Converts a cumulative GPA to a letter grade.
     *
     * @param gpa a cumulative GPA
     * @return the matching letter grade
     */
    public String gpaToLetterGrade(double gpa) {
        return gradingScale.gpaToLetterGrade(gpa);
    }

    /**
     * {@return the best possible GPA on the current scale}
     */
    public double getMaxGpa() {
        return gradingScale.getMaxGpa();
    }
}
