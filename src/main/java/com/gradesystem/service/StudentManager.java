package com.gradesystem.service;

import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.model.Student;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;
import com.gradesystem.util.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Single Responsibility: stores students and finds them by ID.
 *
 * <p>Change from Lab 1: we use an {@link ArrayList} instead of a fixed-size
 * array, so there is no limit of 50 students and no "array full" crash.</p>
 */
public class StudentManager {

    private static final Logger LOGGER = AppLogger.getLogger(StudentManager.class);

    private final List<Student> students = new ArrayList<>();

    /**
     * Creates an empty student store.
     */
    public StudentManager() {
        // nothing to set up
    }

    /**
     * Gives the ID the next student should get: STU001, STU002, ...
     *
     * @return the next free student ID
     */
    public String generateNextId() {
        return String.format(AppConstants.STUDENT_ID_FORMAT, students.size() + 1);
    }

    /**
     * Validates and stores a student.
     *
     * @param student the student to add
     * @throws InvalidInputException if a field is invalid or the ID is already used
     */
    public void addStudent(Student student) throws InvalidInputException {
        InputValidator.validateName(student.getName());
        InputValidator.validateAge(student.getAge());
        InputValidator.validateEmail(student.getEmail());
        InputValidator.validatePhone(student.getPhone());
        if (findById(student.getStudentId()).isPresent()) {
            throw new InvalidInputException("A student with ID " + student.getStudentId() + " already exists.",
                    "Use generateNextId() to get a free ID.");
        }
        students.add(student);
        LOGGER.info("Added student " + student.getStudentId() + " (" + student.getName() + ")");
    }

    /**
     * Finds a student by ID (case-insensitive, spaces ignored).
     *
     * @param studentId for example "STU001" or "stu001"
     * @return the student
     * @throws StudentNotFoundException if no student has that ID
     */
    public Student findStudent(String studentId) throws StudentNotFoundException {
        Optional<Student> student = findById(studentId);
        if (student.isEmpty()) {
            throw new StudentNotFoundException(studentId, getAllStudentIds());
        }
        return student.get();
    }

    /**
     * Same as {@link #findStudent(String)} but returns an empty Optional instead of
     * throwing. Useful for searches where "nothing found" is a normal answer.
     *
     * @param studentId the ID to look for
     * @return the student, or empty
     */
    public Optional<Student> findById(String studentId) {
        String wanted = studentId.trim();
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(wanted)) {
                return Optional.of(student);
            }
        }
        return Optional.empty();
    }

    /**
     * {@return every student, in the order they were added (read-only list)}
     */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    /**
     * {@return every student ID, for example [STU001, STU002]}
     */
    public List<String> getAllStudentIds() {
        List<String> ids = new ArrayList<>();
        for (Student student : students) {
            ids.add(student.getStudentId());
        }
        return ids;
    }

    /**
     * Finds every student of one type.
     *
     * @param studentType "Regular" or "Honors" (case-insensitive)
     * @return the students of that type
     */
    public List<Student> getStudentsOfType(String studentType) {
        List<Student> result = new ArrayList<>();
        for (Student student : students) {
            if (student.getStudentType().equalsIgnoreCase(studentType)) {
                result.add(student);
            }
        }
        return result;
    }

    /**
     * {@return how many students are stored}
     */
    public int getStudentCount() {
        return students.size();
    }
}
