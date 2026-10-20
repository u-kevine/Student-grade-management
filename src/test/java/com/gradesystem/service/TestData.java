package com.gradesystem.service;

import com.gradesystem.model.HonorsStudent;
import com.gradesystem.model.RegularStudent;
import com.gradesystem.model.Student;

/**
 * Small helpers that create test students, so every test does not repeat the same lines.
 */
final class TestData {

    private TestData() {
    }

    static Student regular(String id, String name) {
        return new RegularStudent(id, name, 16, "student@school.edu", "0788000000");
    }

    static Student honors(String id, String name) {
        return new HonorsStudent(id, name, 17, "student@school.edu", "0788000000");
    }
}
