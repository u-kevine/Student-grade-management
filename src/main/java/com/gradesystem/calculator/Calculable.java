package com.gradesystem.calculator;

import com.gradesystem.model.Grade;

import java.util.List;

/**
 * Something that turns a list of grades into one number.
 *
 * <p>Interface Segregation: code that only needs "give me one number for
 * these grades" depends on this tiny interface, not on a big class.</p>
 */
public interface Calculable {

    /**
     * Calculates one result from a list of grades.
     *
     * @param grades the grades
     * @return the result (0 when the list is empty)
     */
    double calculate(List<Grade> grades);
}
