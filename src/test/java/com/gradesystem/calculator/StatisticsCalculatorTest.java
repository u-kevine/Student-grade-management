package com.gradesystem.calculator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StatisticsCalculatorTest {

    private final StatisticsCalculator calc = new StatisticsCalculator();

    @Nested
    class MeanAndMedian {

        @Test
        void meanOfSimpleList() {
            assertEquals(80.0, calc.mean(List.of(70.0, 80.0, 90.0)), 0.001);
        }

        @Test
        void medianWithOddCountIsTheMiddleValue() {
            assertEquals(80.0, calc.median(List.of(90.0, 70.0, 80.0)), 0.001);
        }

        @Test
        void medianWithEvenCountAveragesTheTwoMiddleValues() {
            assertEquals(2.5, calc.median(List.of(4.0, 1.0, 3.0, 2.0)), 0.001);
        }

        @Test
        void medianDoesNotReorderTheCallersList() {
            List<Double> values = new ArrayList<>(List.of(3.0, 1.0, 2.0));
            calc.median(values);
            assertEquals(List.of(3.0, 1.0, 2.0), values);
        }
    }

    @Nested
    class Mode {

        @Test
        void mostFrequentValueWins() {
            assertEquals(OptionalDouble.of(85.0), calc.mode(List.of(70.0, 85.0, 90.0, 85.0)));
        }

        @Test
        void tieReturnsTheSmallestValue() {
            assertEquals(OptionalDouble.of(70.0), calc.mode(List.of(90.0, 90.0, 70.0, 70.0, 80.0)));
        }

        @Test
        void noRepeatedValueMeansNoMode() {
            assertFalse(calc.mode(List.of(70.0, 80.0, 90.0)).isPresent());
        }
    }

    @Test
    void standardDeviationOfKnownSet() {
        // Classic textbook example: population SD of these 8 numbers is exactly 2
        List<Double> values = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        assertEquals(2.0, calc.standardDeviation(values), 0.0001);
    }

    @Test
    void standardDeviationOfIdenticalValuesIsZero() {
        assertEquals(0.0, calc.standardDeviation(List.of(80.0, 80.0, 80.0)), 0.0001);
    }

    @Test
    void minMaxAndRange() {
        List<Double> values = List.of(42.0, 100.0, 75.0);
        assertEquals(42.0, calc.min(values));
        assertEquals(100.0, calc.max(values));
        assertEquals(58.0, calc.range(values));
    }

    @Test
    void everyMethodReturnsZeroForAnEmptyList() {
        List<Double> empty = List.of();
        assertEquals(0.0, calc.mean(empty));
        assertEquals(0.0, calc.median(empty));
        assertEquals(0.0, calc.standardDeviation(empty));
        assertEquals(0.0, calc.min(empty));
        assertEquals(0.0, calc.max(empty));
        assertEquals(0.0, calc.range(empty));
        assertFalse(calc.mode(empty).isPresent());
    }

    @ParameterizedTest(name = "{0}% is band {1}")
    @CsvSource({"100, A", "90, A", "89.9, B", "80, B", "79, C", "70, C", "69, D", "60, D", "59, F", "0, F"})
    void gradeBands(double grade, String band) {
        assertEquals(band, calc.gradeBand(grade));
    }

    @Test
    void countInBand() {
        List<Double> values = List.of(95.0, 91.0, 85.0, 42.0);
        assertEquals(2, calc.countInBand(values, "A"));
        assertEquals(1, calc.countInBand(values, "B"));
        assertEquals(0, calc.countInBand(values, "C"));
        assertEquals(1, calc.countInBand(values, "F"));
    }
}
