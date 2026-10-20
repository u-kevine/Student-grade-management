package com.gradesystem.model;

/**
 * ABSTRACT parent of {@link CoreSubject} and {@link ElectiveSubject}.
 *
 * <p>Open/Closed: a new subject type (for example "LabSubject") is just a new
 * subclass added to the SubjectCatalog. Menus and imports pick it up automatically.</p>
 */
public abstract class Subject {

    private final String subjectName;
    private final String subjectCode;

    /**
     * Creates a new Subject.
     *
     * @param subjectName for example "Mathematics"
     * @param subjectCode for example "MTH101"
     */
    protected Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }

    /** {@return the subject name} */
    public String getSubjectName() { return subjectName; }

    /** {@return the subject code} */
    public String getSubjectCode() { return subjectCode; }

    /**
     * {@return the type label, for example "Core"}
     */
    public abstract String getSubjectType();

    /**
     * {@return true if every student must take this subject}
     */
    public abstract boolean isMandatory();
}
