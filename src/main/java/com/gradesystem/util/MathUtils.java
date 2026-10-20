package com.gradesystem.util;

import java.util.List;

/**
 * Small math helpers shared by several classes, so the same formula is
 * never written twice.
 */
public final class MathUtils {

    private MathUtils() {
        // utility class: do not instantiate
    }

    /**
     * Calculates the average (mean) of a list of numbers.
     *
     * @param values the numbers to average
     * @return the average, or 0 when the list is empty (avoids dividing by zero)
     */
    public static double average(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (double value : values) {
            total = total + value;
        }
        return total / values.size();
    }

    /**
     * Calculates what percentage {@code part} is of {@code total}.
     *
     * @param part  the part
     * @param total the whole
     * @return the percentage, or 0 when total is 0
     */
    public static double percentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return part * 100.0 / total;
    }
}
