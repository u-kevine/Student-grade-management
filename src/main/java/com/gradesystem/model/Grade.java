package com.gradesystem.model;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.InputValidator;
import com.gradesystem.util.TextFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * One grade a student got in one subject.
 *
 * <p>A Grade can never hold an invalid value: the constructor checks the
 * value and throws {@link InvalidGradeException} if it is not 0-100.</p>
 */
public class Grade implements Exportable {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern(AppConstants.DATE_PATTERN);

    private final String gradeId;
    private final String studentId;
    private final Subject subject;   // a Grade HAS A Subject (composition)
    private final double value;
    private final LocalDate date;

    /**
     * Creates a new Grade.
     *
     * @param gradeId   unique ID such as GRD001
     * @param studentId the student who got the grade
     * @param subject   the subject
     * @param value     the grade, 0 to 100
     * @param date      the day the grade was recorded
     * @throws InvalidGradeException if the value is not between 0 and 100
     */
    public Grade(String gradeId, String studentId, Subject subject, double value, LocalDate date)
            throws InvalidGradeException {
        InputValidator.validateGrade(value);
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.subject = subject;
        this.value = value;
        this.date = date;
    }

    /**
     * {@return "GRD001 | 18-10-2026 | Mathematics | Core | 85"}
     */
    @Override
    public String toExportLine() {
        return gradeId + " | " + getFormattedDate() + " | " + subject.getSubjectName()
                + " | " + subject.getSubjectType() + " | " + TextFormat.number(value);
    }

    /** {@return the unique grade ID} */
    public String getGradeId() { return gradeId; }

    /** {@return the ID of the student who got this grade} */
    public String getStudentId() { return studentId; }

    /** {@return the subject} */
    public Subject getSubject() { return subject; }

    /** {@return the grade value, 0 to 100} */
    public double getValue() { return value; }

    /** {@return the date the grade was recorded} */
    public LocalDate getDate() { return date; }

    /** {@return the date as text, for example 18-10-2026} */
    public String getFormattedDate() { return date.format(DATE_FORMAT); }
}
