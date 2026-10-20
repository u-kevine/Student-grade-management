package com.gradesystem.model;

/**
 * Something that can be found by typing part of its name.
 *
 * <p>Interface Segregation: this interface has ONE small job, so classes
 * only implement what they really need.</p>
 */
public interface Searchable {

    /**
     * Checks whether this object matches a search keyword.
     *
     * @param keyword part of a name, for example "john"
     * @return true if it matches (case-insensitive, partial match)
     */
    boolean matchesKeyword(String keyword);
}
