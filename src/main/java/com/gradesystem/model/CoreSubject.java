package com.gradesystem.model;

import com.gradesystem.util.AppConstants;

/**
 * A core (mandatory) subject
 */
public class CoreSubject extends Subject {

    /**
     * Creates a new CoreSubject.
     *
     * @param subjectName for example "Mathematics"
     * @param subjectCode for example "MTH101"
     */
    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }

    @Override
    public String getSubjectType() {
        return AppConstants.TYPE_CORE;
    }

    @Override
    public boolean isMandatory() {
        return true;
    }
}
