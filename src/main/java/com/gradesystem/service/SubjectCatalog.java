package com.gradesystem.service;

import com.gradesystem.exception.SubjectNotFoundException;
import com.gradesystem.model.CoreSubject;
import com.gradesystem.model.ElectiveSubject;
import com.gradesystem.model.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The list of subjects a grade can be recorded for.
 *
 * <p>Open/Closed: menus ask the catalog which types and subjects exist.
 * Adding a new subject or a new Subject subclass only means adding it here.</p>
 */
public class SubjectCatalog {

    private final List<Subject> subjects;

    /**
     * Creates the catalog with the school's default six subjects.
     */
    public SubjectCatalog() {
        this(List.of(
                new CoreSubject("Mathematics", "MTH101"),
                new CoreSubject("English", "ENG101"),
                new CoreSubject("Science", "SCI101"),
                new ElectiveSubject("Music", "MUS101"),
                new ElectiveSubject("Art", "ART101"),
                new ElectiveSubject("Physical Education", "PHE101")));
    }

    /**
     * Creates a catalog with your own subjects (handy for tests).
     *
     * @param subjects the subjects
     */
    public SubjectCatalog(List<Subject> subjects) {
        this.subjects = new ArrayList<>(subjects);
    }

    /**
     * {@return every subject (read-only list)}
     */
    public List<Subject> getAllSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    /**
     * {@return the different subject types, in catalog order, for example [Core, Elective]}
     */
    public List<String> getSubjectTypes() {
        List<String> types = new ArrayList<>();
        for (Subject subject : subjects) {
            if (!types.contains(subject.getSubjectType())) {
                types.add(subject.getSubjectType());
            }
        }
        return types;
    }

    /**
     * Finds every subject of one type.
     *
     * @param subjectType "Core" or "Elective" (case-insensitive)
     * @return the subjects of that type
     */
    public List<Subject> getSubjectsByType(String subjectType) {
        List<Subject> result = new ArrayList<>();
        for (Subject subject : subjects) {
            if (subject.getSubjectType().equalsIgnoreCase(subjectType)) {
                result.add(subject);
            }
        }
        return result;
    }

    /**
     * Finds a subject by name AND type (both case-insensitive).
     *
     * @param subjectName for example "Mathematics"
     * @param subjectType for example "Core"
     * @return the subject
     * @throws SubjectNotFoundException if there is no such subject with that type
     */
    public Subject findSubject(String subjectName, String subjectType) throws SubjectNotFoundException {
        for (Subject subject : subjects) {
            boolean sameName = subject.getSubjectName().equalsIgnoreCase(subjectName.trim());
            boolean sameType = subject.getSubjectType().equalsIgnoreCase(subjectType.trim());
            if (sameName && sameType) {
                return subject;
            }
        }
        List<String> available = new ArrayList<>();
        for (Subject subject : subjects) {
            available.add(subject.getSubjectName() + " (" + subject.getSubjectType() + ")");
        }
        throw new SubjectNotFoundException(subjectName, subjectType, available);
    }
}
