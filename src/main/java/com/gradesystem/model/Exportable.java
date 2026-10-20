package com.gradesystem.model;

/**
 * Something that can be written to a file as one line of text.
 *
 * <p>Interface Segregation: report and export code only needs this one
 * method, not the whole Student or Grade class.</p>
 */
public interface Exportable {

    /**
     * Turns this object into one line of text for a file.
     *
     * @return the line, with values separated by " | "
     */
    String toExportLine();
}
