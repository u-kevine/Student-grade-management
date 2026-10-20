package com.gradesystem.ui;

/**
 * One option of the main menu.
 *
 * <p>Open/Closed: to add a menu option you write a new Screen class and add it
 * to the list in Main. The ConsoleMenu itself never changes.</p>
 */
public interface Screen {

    /**
     * {@return the text shown in the menu, for example "Add Student"}
     */
    String getMenuLabel();

    /**
     * {@return true to show a [NEW] tag next to the option}
     */
    boolean isNew();

    /**
     * Runs this option (asks questions, shows results).
     */
    void show();
}
