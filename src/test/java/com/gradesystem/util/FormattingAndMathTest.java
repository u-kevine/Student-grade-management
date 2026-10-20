package com.gradesystem.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormattingAndMathTest {

    @Test
    void averageAndEmptyAverage() {
        assertEquals(2.0, MathUtils.average(List.of(1.0, 2.0, 3.0)));
        assertEquals(0.0, MathUtils.average(List.of()));
    }

    @Test
    void percentageAndZeroTotal() {
        assertEquals(25.0, MathUtils.percentage(1, 4));
        assertEquals(0.0, MathUtils.percentage(3, 0));
    }

    @Test
    void numbersDropTrailingZero() {
        assertEquals("150", TextFormat.number(150.0));
        assertEquals("72.5", TextFormat.number(72.5));
        assertEquals("Infinity", TextFormat.number(Double.POSITIVE_INFINITY));
    }

    @Test
    void percentAndGpaFormatting() {
        assertEquals("81.2%", TextFormat.percent(81.24));
        assertEquals("3.74", TextFormat.gpa(3.7449));
    }

    @Test
    void barHasFixedLengthAndRightFill() {
        assertEquals("██████████░░░░░░░░░░", TextFormat.bar(50));
        assertEquals("░░░░░░░░░░░░░░░░░░░░", TextFormat.bar(0));
        assertEquals(AppConstants.BAR_LENGTH, TextFormat.bar(100).length());
    }
}
