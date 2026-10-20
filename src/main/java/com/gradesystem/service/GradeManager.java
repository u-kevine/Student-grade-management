package com.gradesystem.service;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Subject;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;
import com.gradesystem.util.MathUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Single Responsibility: stores grades and answers questions about them
 * (counts and averages).
 */
public class GradeManager {

    private static final Logger LOGGER = AppLogger.getLogger(GradeManager.class);

    private final List<Grade> grades = new ArrayList<>();

    /**
     * Creates an empty grade store.
     */
    public GradeManager() {
        // nothing to set up
    }

    /**
     * Creates a grade with a new ID and today's date, then stores it.
     *
     * @param studentId the student
     * @param subject   the subject
     * @param value     the grade, 0 to 100
     * @return the stored grade
     * @throws InvalidGradeException if the value is not between 0 and 100
     */
    public Grade recordGrade(String studentId, Subject subject, double value) throws InvalidGradeException {
        String gradeId = String.format(AppConstants.GRADE_ID_FORMAT, grades.size() + 1);
        Grade grade = new Grade(gradeId, studentId, subject, value, LocalDate.now());
        grades.add(grade);
        LOGGER.info("Recorded " + gradeId + ": " + studentId + " " + subject.getSubjectName() + " = " + value);
        return grade;
    }

    /**
     * {@return every grade, oldest first (read-only list)}
     */
    public List<Grade> getAllGrades() {
        return Collections.unmodifiableList(grades);
    }

    /**
     * Finds every grade of one student.
     *
     * @param studentId the student
     * @return that student's grades, oldest first
     */
    public List<Grade> getGradesForStudent(String studentId) {
        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            if (grade.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(grade);
            }
        }
        return result;
    }

    /**
     * Finds one student's grades for one subject type.
     *
     * @param studentId   the student
     * @param subjectType "Core" or "Elective"
     * @return that student's grades of that type
     */
    public List<Grade> getGradesForStudent(String studentId, String subjectType) {
        List<Grade> result = new ArrayList<>();
        for (Grade grade : getGradesForStudent(studentId)) {
            if (grade.getSubject().getSubjectType().equalsIgnoreCase(subjectType)) {
                result.add(grade);
            }
        }
        return result;
    }

    /**
     * Finds every grade for one subject.
     *
     * @param subjectName for example "Mathematics"
     * @return every grade for that subject (all students)
     */
    public List<Grade> getGradesForSubject(String subjectName) {
        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            if (grade.getSubject().getSubjectName().equalsIgnoreCase(subjectName)) {
                result.add(grade);
            }
        }
        return result;
    }

    /**
     * Finds every grade of one subject type.
     *
     * @param subjectType "Core" or "Elective"
     * @return every grade of that type (all students)
     */
    public List<Grade> getGradesForSubjectType(String subjectType) {
        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            if (grade.getSubject().getSubjectType().equalsIgnoreCase(subjectType)) {
                result.add(grade);
            }
        }
        return result;
    }

    /**
     * Checks whether a student has any grades.
     *
     * @param studentId the student
     * @return true if the student has at least one grade
     */
    public boolean hasGrades(String studentId) {
        return !getGradesForStudent(studentId).isEmpty();
    }

    /**
     * Calculates a student's overall average.
     *
     * @param studentId the student
     * @return the student's overall average, or 0 if there are no grades
     */
    public double calculateAverage(String studentId) {
        return averageOf(getGradesForStudent(studentId));
    }

    /**
     * Calculates a student's average for one subject type.
     *
     * @param studentId   the student
     * @param subjectType "Core" or "Elective"
     * @return the student's average for that type, or 0 if there are none
     */
    public double calculateAverage(String studentId, String subjectType) {
        return averageOf(getGradesForStudent(studentId, subjectType));
    }

    /**
     * Turns a list of grades into a list of plain numbers.
     *
     * @param gradeList the grades
     * @return their values
     */
    public static List<Double> valuesOf(List<Grade> gradeList) {
        List<Double> values = new ArrayList<>();
        for (Grade grade : gradeList) {
            values.add(grade.getValue());
        }
        return values;
    }

    /**
     * Calculates the average of a list of grades.
     *
     * @param gradeList the grades
     * @return their average, or 0 for an empty list
     */
    public static double averageOf(List<Grade> gradeList) {
        return MathUtils.average(valuesOf(gradeList));
    }

    /**
     * {@return how many grades are stored in total}
     */
    public int getTotalGradeCount() {
        return grades.size();
    }
}
