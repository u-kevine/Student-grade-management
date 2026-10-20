package com.gradesystem.calculator;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Subject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GPACalculatorTest {

    private final Subject math = new CoreSubject("Mathematics", "MTH101");
    private final GPACalculator calculator = new GPACalculator(new FourPointGradingScale());

    private Grade grade(double value) throws InvalidGradeException {
        return new Grade("GRD001", "STU001", math, value, LocalDate.now());
    }

    @Test
    void cumulativeGpaIsTheAverageOfGradePoints() throws InvalidGradeException {
        // 95 -> 4.0, 85 -> 3.0, 75 -> 2.0  => (4 + 3 + 2) / 3 = 3.0
        List<Grade> grades = List.of(grade(95), grade(85), grade(75));
        assertEquals(3.0, calculator.calculate(grades), 0.001);
    }

    @Test
    void emptyListGivesZeroInsteadOfCrashing() {
        assertEquals(0.0, calculator.calculate(List.of()));
    }

    @Test
    void allFailingGradesGiveZero() throws InvalidGradeException {
        assertEquals(0.0, calculator.calculate(List.of(grade(10), grade(59))));
    }

    @Test
    void delegatesToTheInjectedScale() throws InvalidGradeException {
        // A mock scale proves the calculator really uses what we inject (Dependency Inversion).
        GradingScale fakeScale = mock(GradingScale.class);
        when(fakeScale.toGpaPoints(50.0)).thenReturn(5.0);
        when(fakeScale.toLetterGrade(50.0)).thenReturn("X");
        when(fakeScale.gpaToLetterGrade(5.0)).thenReturn("Y");
        when(fakeScale.getMaxGpa()).thenReturn(5.0);

        GPACalculator custom = new GPACalculator(fakeScale);

        assertEquals(5.0, custom.calculate(List.of(grade(50))));
        assertEquals(5.0, custom.toGpaPoints(50.0));
        assertEquals("X", custom.toLetterGrade(50.0));
        assertEquals("Y", custom.gpaToLetterGrade(5.0));
        assertEquals(5.0, custom.getMaxGpa());
        verify(fakeScale).toLetterGrade(50.0);
    }
}
