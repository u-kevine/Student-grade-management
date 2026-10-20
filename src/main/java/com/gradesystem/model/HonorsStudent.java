package com.gradesystem.model;

import com.gradesystem.util.AppConstants;

/**
 * An honors student: passing grade 60%, honors eligible with an average of 85% or more.
 */
public class HonorsStudent extends Student {

    /**
     * Creates a new HonorsStudent.
     *
     * @param studentId unique ID such as STU002
     * @param name      full name
     * @param age       age in years
     * @param email     email address
     * @param phone     phone number
     */
    public HonorsStudent(String studentId, String name, int age, String email, String phone) {
        super(studentId, name, age, email, phone);
    }

    @Override
    public String getStudentType() {
        return AppConstants.TYPE_HONORS;
    }

    @Override
    public double getPassingGrade() {
        return AppConstants.HONORS_PASSING_GRADE;
    }

    /**
     * Honors students stay eligible while their average is at least 85%.
     *
     * @param average the student's average grade
     * @return true when the average is 85 or more
     */
    @Override
    public boolean isHonorsEligible(double average) {
        return average >= AppConstants.HONORS_ELIGIBILITY_AVERAGE;
    }
}
