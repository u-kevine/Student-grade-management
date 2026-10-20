package com.gradesystem.service;

import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.model.Student;
import com.gradesystem.util.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Finds students by ID, name, grade range or type.
 *
 * <p>Dependency Injection: the managers are passed in through the constructor,
 * so tests can pass in mocks.</p>
 */
public class SearchService {

    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    /**
     * Creates a new SearchService.
     *
     * @param studentManager where the students are stored
     * @param gradeManager   where the grades are stored
     */
    public SearchService(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    /**
     * Exact match on the student ID.
     *
     * @param studentId for example "STU001"
     * @return a list with the student, or an empty list
     */
    public List<Student> searchById(String studentId) {
        List<Student> result = new ArrayList<>();
        Optional<Student> student = studentManager.findById(studentId);
        student.ifPresent(result::add);
        return result;
    }

    /**
     * Partial, case-insensitive match on the name: "john" finds "Alice Johnson".
     *
     * @param keyword part of a name
     * @return the matching students
     * @throws InvalidInputException if the keyword is empty
     */
    public List<Student> searchByName(String keyword) throws InvalidInputException {
        if (keyword == null || keyword.isBlank()) {
            throw new InvalidInputException("Search text cannot be empty.", "Type part of a name, e.g. John");
        }
        List<Student> result = new ArrayList<>();
        for (Student student : studentManager.getAllStudents()) {
            if (student.matchesKeyword(keyword)) {
                result.add(student);
            }
        }
        return result;
    }

    /**
     * Students whose overall average is between min and max (both included).
     * Students without grades are skipped.
     *
     * @param min lowest average, for example 80
     * @param max highest average, for example 90
     * @return the matching students
     * @throws InvalidInputException if the range is not valid
     */
    public List<Student> searchByGradeRange(double min, double max) throws InvalidInputException {
        InputValidator.validateGradeRange(min, max);
        List<Student> result = new ArrayList<>();
        for (Student student : studentManager.getAllStudents()) {
            String id = student.getStudentId();
            if (gradeManager.hasGrades(id)) {
                double average = gradeManager.calculateAverage(id);
                if (average >= min && average <= max) {
                    result.add(student);
                }
            }
        }
        return result;
    }

    /**
     * Finds every student of one type.
     *
     * @param studentType "Regular" or "Honors"
     * @return the students of that type
     */
    public List<Student> searchByType(String studentType) {
        return studentManager.getStudentsOfType(studentType);
    }
}
