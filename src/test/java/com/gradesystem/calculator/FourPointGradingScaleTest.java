package com.gradesystem.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks every row of the 4.0 scale, including the exact edges of each band
 * (for example 92.9 must still be A-, 93 must be A).
 */
class FourPointGradingScaleTest {

    private final GradingScale scale = new FourPointGradingScale();

    @ParameterizedTest(name = "{0}% -> {1} ({2})")
    @CsvSource({
            "100, 4.0, A", "93, 4.0, A",
            "92.9, 3.7, A-", "90, 3.7, A-",
            "89, 3.3, B+", "87, 3.3, B+",
            "86, 3.0, B", "83, 3.0, B",
            "82, 2.7, B-", "80, 2.7, B-",
            "79, 2.3, C+", "77, 2.3, C+",
            "76, 2.0, C", "73, 2.0, C",
            "72, 1.7, C-", "70, 1.7, C-",
            "69, 1.3, D+", "67, 1.3, D+",
            "66, 1.0, D", "60, 1.0, D",
            "59.9, 0.0, F", "0, 0.0, F"
    })
    @DisplayName("percentage converts to the right GPA points and letter")
    void convertsPercentage(double percentage, double expectedPoints, String expectedLetter) {
        assertEquals(expectedPoints, scale.toGpaPoints(percentage), 0.001);
        assertEquals(expectedLetter, scale.toLetterGrade(percentage));
    }

    @ParameterizedTest(name = "GPA {0} -> {1}")
    @CsvSource({"4.0, A", "3.74, A-", "3.28, B", "2.88, B-", "1.0, D", "0.99, F", "0.0, F"})
    void convertsCumulativeGpaToLetter(double gpa, String expectedLetter) {
        assertEquals(expectedLetter, scale.gpaToLetterGrade(gpa));
    }

    @Test
    void maxGpaIsFour() {
        assertEquals(4.0, scale.getMaxGpa());
    }
}
