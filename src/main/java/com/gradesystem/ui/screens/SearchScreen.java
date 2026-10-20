package com.gradesystem.ui.screens;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.model.Student;
import com.gradesystem.service.ExportResult;
import com.gradesystem.service.ExportService;
import com.gradesystem.service.GradeManager;
import com.gradesystem.service.SearchService;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentDetailsView;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.InputValidator;
import com.gradesystem.util.TextFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Menu option 9: Search Students (US-6).
 */
public class SearchScreen implements Screen {

    private static final String ROW_FORMAT = "%-7s | %-17s | %-8s | %s%n";
    private static final int ACTION_VIEW = 1;
    private static final int ACTION_EXPORT = 2;
    private static final int ACTION_NEW_SEARCH = 3;
    private static final int ACTION_MAIN_MENU = 4;

    private final ConsoleIO io;
    private final SearchService searchService;
    private final GradeManager gradeManager;
    private final ExportService exportService;
    private final StudentDetailsView detailsView;

    /** Text describing the last search, used as the title of an exported file. */
    private String lastCriteria = "";

    /**
     * Creates a new SearchScreen.
     *
     * @param io            console input/output
     * @param searchService does the searching
     * @param gradeManager  used to show averages
     * @param exportService saves search results to a file
     * @param detailsView   shows one student's details
     */
    public SearchScreen(ConsoleIO io, SearchService searchService, GradeManager gradeManager,
                        ExportService exportService, StudentDetailsView detailsView) {
        this.io = io;
        this.searchService = searchService;
        this.gradeManager = gradeManager;
        this.exportService = exportService;
        this.detailsView = detailsView;
    }

    @Override
    public String getMenuLabel() {
        return "Search Students";
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void show() {
        boolean searching = true;
        while (searching) {
            io.printTitle("SEARCH STUDENTS");
            Optional<List<Student>> results = runSearch();
            if (results.isEmpty()) {
                searching = io.readYesNo("Search again? (Y/N): ");
            } else {
                showResults(results.get());
                searching = handleActions(results.get());
            }
            io.println();
        }
    }

    /** {@return the results, or empty if the search input was invalid} */
    private Optional<List<Student>> runSearch() {
        io.println("Search options:");
        io.println("1. By Student ID");
        io.println("2. By Name (partial match)");
        io.println("3. By Grade Range");
        io.println("4. By Student Type");
        int option = io.readInt("Select option (1-4): ", 1, 4);
        io.println();
        try {
            if (option == 1) {
                String id = io.readText("Enter Student ID: ");
                lastCriteria = "Student ID = " + id;
                return Optional.of(searchService.searchById(id));
            } else if (option == 2) {
                String name = io.readText("Enter name (partial or full): ");
                lastCriteria = "Name contains '" + name + "'";
                return Optional.of(searchService.searchByName(name));
            } else if (option == 3) {
                double min = io.readDouble("Enter minimum average (0-100): ");
                double max = io.readDouble("Enter maximum average (0-100): ");
                lastCriteria = "Average between " + TextFormat.number(min) + "% and " + TextFormat.number(max) + "%";
                return Optional.of(searchService.searchByGradeRange(min, max));
            } else {
                io.println("1. Regular");
                io.println("2. Honors");
                int type = io.readInt("Select type (1-2): ", 1, 2);
                String typeName = AppConstants.TYPE_REGULAR;
                if (type == 2) {
                    typeName = AppConstants.TYPE_HONORS;
                }
                lastCriteria = "Type = " + typeName;
                return Optional.of(searchService.searchByType(typeName));
            }
        } catch (InvalidInputException e) {
            io.showError(e);
            return Optional.empty();
        }
    }

    private void showResults(List<Student> results) {
        io.println();
        io.println("SEARCH RESULTS (" + results.size() + " found)");
        io.println(AppConstants.LINE);
        if (results.isEmpty()) {
            io.println("No students matched: " + lastCriteria);
            io.println(AppConstants.LINE);
            return;
        }
        io.printf(ROW_FORMAT, "STU ID", "NAME", "TYPE", "AVG");
        io.println(AppConstants.LINE);
        for (Student student : results) {
            String average = "N/A";
            if (gradeManager.hasGrades(student.getStudentId())) {
                average = TextFormat.percent(gradeManager.calculateAverage(student.getStudentId()));
            }
            io.printf(ROW_FORMAT, student.getStudentId(), student.getName(), student.getStudentType(), average);
        }
        io.println(AppConstants.LINE);
    }

    /** {@return true to search again, false to go back to the main menu} */
    private boolean handleActions(List<Student> results) {
        while (true) {
            io.println();
            io.println("Actions:");
            io.println("1. View full details for a student");
            io.println("2. Export search results");
            io.println("3. New search");
            io.println("4. Return to main menu");
            int action = io.readInt("Enter choice: ", ACTION_VIEW, ACTION_MAIN_MENU);
            io.println();
            if (action == ACTION_VIEW) {
                viewDetails(results);
            } else if (action == ACTION_EXPORT) {
                exportResults(results);
            } else {
                return action == ACTION_NEW_SEARCH;
            }
        }
    }

    private void viewDetails(List<Student> results) {
        if (results.isEmpty()) {
            io.println("There are no results to view.");
            return;
        }
        List<String> ids = new ArrayList<>();
        for (Student student : results) {
            ids.add(student.getStudentId());
        }
        String id = io.readText("Enter Student ID from the results (" + String.join(", ", ids) + "): ");
        for (Student student : results) {
            if (student.getStudentId().equalsIgnoreCase(id)) {
                io.println();
                detailsView.show(student);
                return;
            }
        }
        io.println("'" + id + "' is not in these search results.");
    }

    private void exportResults(List<Student> results) {
        String fileName = io.readValidText("Enter filename (without extension): ", InputValidator::validateFileName);
        try {
            ExportResult result = exportService.exportSearchResults(results, lastCriteria, fileName);
            io.println();
            io.showSuccess("Search results exported to " + result.getLocation() + result.getFileName()
                    + " (" + result.getFormattedSize() + ")");
        } catch (InvalidInputException | FileExportException e) {
            io.showError(e);
        }
    }
}
