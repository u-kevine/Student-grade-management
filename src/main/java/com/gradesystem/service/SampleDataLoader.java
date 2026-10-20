package com.gradesystem.service;

import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.SubjectNotFoundException;
import com.gradesystem.model.HonorsStudent;
import com.gradesystem.model.RegularStudent;
import com.gradesystem.model.Subject;

/**
 * Fills the system with 5 students and 25 grades so every feature can be
 * tried straight away (statistics, GPA rank, search, ...).
 */
public class SampleDataLoader {

    /** Each line uses the same layout as an import CSV: StudentID,SubjectName,SubjectType,Grade */
    private static final String[] SAMPLE_GRADES = {
            "STU001,Mathematics,Core,78", "STU001,English,Core,85", "STU001,Science,Core,80",
            "STU001,Music,Elective,88", "STU001,Art,Elective,75",
            "STU002,Mathematics,Core,92", "STU002,English,Core,85", "STU002,Science,Core,88",
            "STU002,Music,Elective,91", "STU002,Art,Elective,81",
            "STU003,Mathematics,Core,42", "STU003,English,Core,68", "STU003,Science,Core,71",
            "STU003,Physical Education,Elective,65", "STU003,Art,Elective,77",
            "STU004,Mathematics,Core,95", "STU004,English,Core,90", "STU004,Science,Core,100",
            "STU004,Music,Elective,85", "STU004,Physical Education,Elective,89",
            "STU005,Mathematics,Core,74", "STU005,English,Core,82", "STU005,Science,Core,85",
            "STU005,Music,Elective,70", "STU005,Physical Education,Elective,76"
    };

    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final SubjectCatalog subjectCatalog;

    /**
     * Creates a new SampleDataLoader.
     *
     * @param studentManager where students are added
     * @param gradeManager   where grades are added
     * @param subjectCatalog where subjects are looked up
     */
    public SampleDataLoader(StudentManager studentManager, GradeManager gradeManager, SubjectCatalog subjectCatalog) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.subjectCatalog = subjectCatalog;
    }

    /**
     * Adds the sample students (STU001-STU005) and their grades.
     * Call this on an EMPTY system, otherwise the IDs will not line up.
     *
     * @throws InvalidInputException    if a sample student is invalid (should never happen)
     * @throws InvalidGradeException    if a sample grade is invalid (should never happen)
     * @throws SubjectNotFoundException if a sample subject is missing from the catalog
     */
    public void load() throws InvalidInputException, InvalidGradeException, SubjectNotFoundException {
        studentManager.addStudent(new RegularStudent(studentManager.generateNextId(),
                "Alice Johnson", 16, "alice.johnson@school.edu", "0788000001"));
        studentManager.addStudent(new HonorsStudent(studentManager.generateNextId(),
                "Bob Johnson", 17, "bob.johnson@school.edu", "0788000002"));
        studentManager.addStudent(new RegularStudent(studentManager.generateNextId(),
                "Carol Martinez", 16, "carol.martinez@school.edu", "0788000003"));
        studentManager.addStudent(new HonorsStudent(studentManager.generateNextId(),
                "David Lee", 17, "david.lee@school.edu", "0788000004"));
        studentManager.addStudent(new RegularStudent(studentManager.generateNextId(),
                "Emma Wilson", 15, "emma.wilson@school.edu", "0788000005"));

        for (String line : SAMPLE_GRADES) {
            String[] parts = line.split(",");
            Subject subject = subjectCatalog.findSubject(parts[1], parts[2]);
            gradeManager.recordGrade(parts[0], subject, Double.parseDouble(parts[3]));
        }
    }
}
