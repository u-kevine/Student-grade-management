package com.gradesystem.service;

import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.Grade;
import com.gradesystem.model.Student;
import com.gradesystem.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates a student's GPA, class rank and the class average GPA.
 * The math itself is done by the injected {@link GPACalculator}.
 */
public class GPAService {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final GPACalculator gpaCalculator;

    /**
     * Creates a new GPAService.
     *
     * @param studentManager where the students are stored
     * @param gradeManager   where the grades are stored
     * @param gpaCalculator  does the GPA math
     */
    public GPAService(StudentManager studentManager, GradeManager gradeManager, GPACalculator gpaCalculator) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.gpaCalculator = gpaCalculator;
    }

    /**
     * Calculates one student's cumulative GPA.
     *
     * @param studentId the student
     * @return the cumulative GPA
     * @throws StudentNotFoundException if the student does not exist
     * @throws NoGradesException        if the student has no grades yet
     */
    public double calculateGpa(String studentId) throws StudentNotFoundException, NoGradesException {
        Student student = studentManager.findStudent(studentId);
        List<Grade> grades = gradeManager.getGradesForStudent(student.getStudentId());
        if (grades.isEmpty()) {
            throw new NoGradesException(student.getStudentId());
        }
        return gpaCalculator.calculate(grades);
    }

    /**
     * Rank 1 = highest GPA. Students with the same GPA share the same rank.
     * Only students with grades are ranked.
     *
     * @param studentId the student
     * @return the student's rank
     * @throws StudentNotFoundException if the student does not exist
     * @throws NoGradesException        if the student has no grades yet
     */
    public int getClassRank(String studentId) throws StudentNotFoundException, NoGradesException {
        double myGpa = calculateGpa(studentId);
        int rank = 1;
        for (double otherGpa : getAllGpas()) {
            if (otherGpa > myGpa) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * {@return how many students have at least one grade (and so are ranked)}
     */
    public int getRankedStudentCount() {
        return getAllGpas().size();
    }

    /**
     * {@return the average GPA of all students that have grades, or 0 if none}
     */
    public double getClassAverageGpa() {
        return MathUtils.average(getAllGpas());
    }

    private List<Double> getAllGpas() {
        List<Double> gpas = new ArrayList<>();
        for (Student student : studentManager.getAllStudents()) {
            List<Grade> grades = gradeManager.getGradesForStudent(student.getStudentId());
            if (!grades.isEmpty()) {
                gpas.add(gpaCalculator.calculate(grades));
            }
        }
        return gpas;
    }

    /**
     * Gives access to the calculator so screens can convert single grades.
     *
     * @return the GPA calculator
     */
    public GPACalculator getGpaCalculator() {
        return gpaCalculator;
    }
}
