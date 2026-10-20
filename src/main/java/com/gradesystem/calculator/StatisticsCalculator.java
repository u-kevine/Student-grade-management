package com.gradesystem.calculator;

import com.gradesystem.util.AppConstants;
import com.gradesystem.util.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

/**
 * Single Responsibility: this class ONLY does statistics math on a list of numbers.
 * It knows nothing about students or files, which makes it very easy to test.
 *
 * <p>Every method returns 0 for an empty list instead of crashing.</p>
 */
public class StatisticsCalculator {

    /**
     * Creates a statistics calculator.
     */
    public StatisticsCalculator() {
        // nothing to set up
    }

    /**
     * Calculates the average (mean).
     *
     * @param values the numbers
     * @return the average
     */
    public double mean(List<Double> values) {
        return MathUtils.average(values);
    }

    /**
     * The middle value after sorting. For an even count it is the
     * average of the two middle values: [1, 2, 3, 4] → 2.5
     *
     * @param values the numbers
     * @return the median
     */
    public double median(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        List<Double> sorted = new ArrayList<>(values);   // copy, so we do not change the caller's list
        Collections.sort(sorted);
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        }
        return sorted.get(middle);
    }

    /**
     * The value that appears most often. If several values tie, the smallest is returned.
     *
     * @param values the numbers
     * @return the mode, or empty when no value appears more than once
     */
    public OptionalDouble mode(List<Double> values) {
        Map<Double, Integer> counts = new HashMap<>();
        for (double value : values) {
            counts.put(value, counts.getOrDefault(value, 0) + 1);
        }
        double bestValue = 0.0;
        int bestCount = 1;   // must beat 1, otherwise there is no mode
        for (Map.Entry<Double, Integer> entry : counts.entrySet()) {
            boolean moreOften = entry.getValue() > bestCount;
            boolean sameButSmaller = entry.getValue() == bestCount && bestCount > 1 && entry.getKey() < bestValue;
            if (moreOften || sameButSmaller) {
                bestValue = entry.getKey();
                bestCount = entry.getValue();
            }
        }
        if (bestCount == 1) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(bestValue);
    }

    /**
     * Population standard deviation: how spread out the numbers are.
     * Steps: find the mean, square each distance from it, average those squares, take the square root.
     *
     * @param values the numbers
     * @return the standard deviation
     */
    public double standardDeviation(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        double mean = mean(values);
        List<Double> squaredDistances = new ArrayList<>();
        for (double value : values) {
            double distance = value - mean;
            squaredDistances.add(distance * distance);
        }
        return Math.sqrt(MathUtils.average(squaredDistances));
    }

    /**
     * Finds the smallest number.
     *
     * @param values the numbers
     * @return the smallest number
     */
    public double min(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return Collections.min(values);
    }

    /**
     * Finds the biggest number.
     *
     * @param values the numbers
     * @return the biggest number
     */
    public double max(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return Collections.max(values);
    }

    /**
     * Calculates the range (biggest minus smallest).
     *
     * @param values the numbers
     * @return biggest minus smallest
     */
    public double range(List<Double> values) {
        return max(values) - min(values);
    }

    /**
     * Puts a grade in a distribution band: A (90-100), B (80-89), C (70-79), D (60-69), F (below 60).
     *
     * @param grade a grade from 0 to 100
     * @return the band letter
     */
    public String gradeBand(double grade) {
        if (grade >= AppConstants.BAND_A_MIN) {
            return "A";
        } else if (grade >= AppConstants.BAND_B_MIN) {
            return "B";
        } else if (grade >= AppConstants.BAND_C_MIN) {
            return "C";
        } else if (grade >= AppConstants.BAND_D_MIN) {
            return "D";
        } else {
            return "F";
        }
    }

    /**
     * Counts how many grades fall in a band.
     *
     * @param values the grades
     * @param band   "A", "B", "C", "D" or "F"
     * @return how many grades are in that band
     */
    public int countInBand(List<Double> values, String band) {
        int count = 0;
        for (double value : values) {
            if (gradeBand(value).equals(band)) {
                count++;
            }
        }
        return count;
    }
}
