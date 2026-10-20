package com.gradesystem.model;

import com.gradesystem.util.AppConstants;

/**
 * An elective (optional) subject such as Music.
 */
public class ElectiveSubject extends Subject {

    /**
     * Creates a new ElectiveSubject.
     *
     * @param subjectName for example "Music"
     * @param subjectCode for example "MUS101"
     */
    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public String getSubjectType() {
        return AppConstants.TYPE_ELECTIVE;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }
}
