package com.gradesystem.service;

import com.gradesystem.calculator.StatisticsCalculator;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Collects class-wide numbers (distribution, mean, median, ...) for the
 * statistics screen. The math is done by the injected {@link StatisticsCalculator}.
 */
public class StatisticsService {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final StatisticsCalculator calculator;

    /**
     * Creates a new StatisticsService.
     *
     * @param studentManager where the students are stored
     * @param gradeManager   where the grades are stored
     * @param calculator     does the statistics math
     */
    public StatisticsService(StudentManager studentManager, GradeManager gradeManager,
                             StatisticsCalculator calculator) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.calculator = calculator;
    }

    /** {@return every grade value in the class} */
    public List<Double> getAllGradeValues() {
        return GradeManager.valuesOf(gradeManager.getAllGrades());
    }

    /** {@return number of grades recorded} */
    public int getTotalGrades() {
        return gradeManager.getTotalGradeCount();
    }

    /** {@return number of students} */
    public int getTotalStudents() {
        return studentManager.getStudentCount();
    }

    /**
     * Counts the grades in one distribution band.
     *
     * @param band "A", "B", "C", "D" or "F"
     * @return how many grades are in that band
     */
    public int getBandCount(String band) {
        return calculator.countInBand(getAllGradeValues(), band);
    }

    /** {@return the class mean} */
    public double getMean() { return calculator.mean(getAllGradeValues()); }

    /** {@return the class median} */
    public double getMedian() { return calculator.median(getAllGradeValues()); }

    /** {@return the class mode, or empty if no grade repeats} */
    public OptionalDouble getMode() { return calculator.mode(getAllGradeValues()); }

    /** {@return the class standard deviation} */
    public double getStandardDeviation() { return calculator.standardDeviation(getAllGradeValues()); }

    /** {@return the lowest grade value} */
    public double getMin() { return calculator.min(getAllGradeValues()); }

    /** {@return the highest grade value} */
    public double getMax() { return calculator.max(getAllGradeValues()); }

    /** {@return highest minus lowest} */
    public double getRange() { return calculator.range(getAllGradeValues()); }

    /**
     * {@return the grade with the highest value (the first one if several tie)}
     */
    public Optional<Grade> getHighestGrade() {
        Grade best = null;
        for (Grade grade : gradeManager.getAllGrades()) {
            if (best == null || grade.getValue() > best.getValue()) {
                best = grade;
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * {@return the grade with the lowest value (the first one if several tie)}
     */
    public Optional<Grade> getLowestGrade() {
        Grade worst = null;
        for (Grade grade : gradeManager.getAllGrades()) {
            if (worst == null || grade.getValue() < worst.getValue()) {
                worst = grade;
            }
        }
        return Optional.ofNullable(worst);
    }

    /**
     * Calculates the class average for one subject.
     *
     * @param subjectName for example "Mathematics"
     * @return the class average for that subject, or empty if nobody has a grade in it
     */
    public OptionalDouble getSubjectAverage(String subjectName) {
        return averageOrEmpty(gradeManager.getGradesForSubject(subjectName));
    }

    /**
     * Calculates the class average for one subject type.
     *
     * @param subjectType "Core" or "Elective"
     * @return the class average for that type, or empty if there are no such grades
     */
    public OptionalDouble getSubjectTypeAverage(String subjectType) {
        return averageOrEmpty(gradeManager.getGradesForSubjectType(subjectType));
    }

    /**
     * Average of the student averages for one student type.
     * Students without grades are skipped.
     *
     * @param studentType "Regular" or "Honors"
     * @return the average, or empty if no student of that type has grades
     */
    public OptionalDouble getStudentTypeAverage(String studentType) {
        return averageOfStudentAverages(studentManager.getStudentsOfType(studentType));
    }

    /**
     * Counts the students of one type.
     *
     * @param studentType "Regular" or "Honors"
     * @return how many students have that type
     */
    public int countStudentsOfType(String studentType) {
        return studentManager.getStudentsOfType(studentType).size();
    }

    /**
     * Average of all student averages (students without grades are skipped).
     *
     * @return the class average, or 0 if nobody has grades
     */
    public double getClassAverage() {
        return averageOfStudentAverages(studentManager.getAllStudents()).orElse(0.0);
    }

    private OptionalDouble averageOfStudentAverages(List<Student> students) {
        List<Double> averages = new ArrayList<>();
        for (Student student : students) {
            if (gradeManager.hasGrades(student.getStudentId())) {
                averages.add(gradeManager.calculateAverage(student.getStudentId()));
            }
        }
        if (averages.isEmpty()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(MathUtils.average(averages));
    }

    private OptionalDouble averageOrEmpty(List<Grade> grades) {
        if (grades.isEmpty()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(GradeManager.averageOf(grades));
    }
}
