package com.gradesystem.calculator;

/**
 * The standard 4.0 GPA scale from the project brief.
 *
 * <pre>
 * 93-100 = 4.0 A    80-82 = 2.7 B-   70-72 = 1.7 C-
 * 90-92  = 3.7 A-   77-79 = 2.3 C+   67-69 = 1.3 D+
 * 87-89  = 3.3 B+   73-76 = 2.0 C    60-66 = 1.0 D
 * 83-86  = 3.0 B                     below 60 = 0.0 F
 * </pre>
 *
 * <p>The table is stored in three arrays that line up: position 0 of each
 * array belongs together (93% → 4.0 → "A"), position 1 belongs together, and so on.</p>
 */
public class FourPointGradingScale implements GradingScale {

    private static final double MAX_GPA = 4.0;
    private static final double FAILING_POINTS = 0.0;
    private static final String FAILING_LETTER = "F";

    private static final double[] MIN_PERCENTAGES = {93, 90, 87, 83, 80, 77, 73, 70, 67, 60};
    private static final double[] GPA_POINTS = {4.0, 3.7, 3.3, 3.0, 2.7, 2.3, 2.0, 1.7, 1.3, 1.0};
    private static final String[] LETTERS = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D"};

    /**
     * Creates the standard 4.0 grading scale.
     */
    public FourPointGradingScale() {
        // nothing to set up
    }

    @Override
    public double toGpaPoints(double percentage) {
        int row = findRowForPercentage(percentage);
        if (row == -1) {
            return FAILING_POINTS;
        }
        return GPA_POINTS[row];
    }

    @Override
    public String toLetterGrade(double percentage) {
        int row = findRowForPercentage(percentage);
        if (row == -1) {
            return FAILING_LETTER;
        }
        return LETTERS[row];
    }

    @Override
    public String gpaToLetterGrade(double gpa) {
        // Walk down the table and stop at the first GPA we reach. 3.74 → 3.7 → "A-"
        for (int i = 0; i < GPA_POINTS.length; i++) {
            if (gpa >= GPA_POINTS[i]) {
                return LETTERS[i];
            }
        }
        return FAILING_LETTER;
    }

    @Override
    public double getMaxGpa() {
        return MAX_GPA;
    }

    /**
     * Finds which row of the table a percentage belongs to.
     *
     * @return the row number, or -1 when the grade is below 60 (an F)
     */
    private int findRowForPercentage(double percentage) {
        for (int i = 0; i < MIN_PERCENTAGES.length; i++) {
            if (percentage >= MIN_PERCENTAGES[i]) {
                return i;
            }
        }
        return -1;
    }
}
