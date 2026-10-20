package com.gradesystem.ui;

import com.gradesystem.exception.InputClosedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the main menu and runs the chosen screen, until the user picks Exit.
 */
public class ConsoleMenu {

    private static final int LABEL_WIDTH = 28;

    private final ConsoleIO io;
    private final List<Screen> screens;

    /**
     * Creates a new ConsoleMenu.
     *
     * @param io      console input/output
     * @param screens the menu options, in the order they are shown (Exit is added automatically)
     */
    public ConsoleMenu(ConsoleIO io, List<Screen> screens) {
        this.io = io;
        this.screens = new ArrayList<>(screens);
    }

    /**
     * Runs the menu loop. Ends when the user picks Exit or the input is closed.
     */
    public void run() {
        int exitOption = screens.size() + 1;
        try {
            boolean running = true;
            while (running) {
                showMenu();
                int choice = io.readInt("Enter choice: ", 1, exitOption);
                io.println();
                if (choice == exitOption) {
                    running = false;
                } else {
                    screens.get(choice - 1).show();
                }
            }
        } catch (InputClosedException e) {
            io.println();
            io.println("Input closed.");
        }
        io.println("Thank you for using Student Grade Management System!");
        io.println("Goodbye!");
    }

    private void showMenu() {
        io.println();
        io.println("╔════════════════════════════════════════════╗");
        io.println("║   STUDENT GRADE MANAGEMENT - MAIN MENU     ║");
        io.println("╚════════════════════════════════════════════╝");
        io.println();
        for (int i = 0; i < screens.size(); i++) {
            Screen screen = screens.get(i);
            String label = (i + 1) + ". " + screen.getMenuLabel();
            if (screen.isNew()) {
                io.printf("%-" + LABEL_WIDTH + "s [NEW]%n", label);
            } else {
                io.println(label);
            }
        }
        io.println((screens.size() + 1) + ". Exit");
        io.println();
    }
}
