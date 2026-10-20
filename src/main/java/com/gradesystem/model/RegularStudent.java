package com.gradesystem.model;

import com.gradesystem.util.AppConstants;

/**
 * A regular student: passing grade 50%.
 */
public class RegularStudent extends Student {

    /**
     * Creates a new RegularStudent.
     *
     * @param studentId unique ID such as STU001
     * @param name      full name
     * @param age       age in years
     * @param email     email address
     * @param phone     phone number
     */
    public RegularStudent(String studentId, String name, int age, String email, String phone) {
        super(studentId, name, age, email, phone);
    }

    @Override
    public String getStudentType() {
        return AppConstants.TYPE_REGULAR;
    }

    @Override
    public double getPassingGrade() {
        return AppConstants.REGULAR_PASSING_GRADE;
    }
}
