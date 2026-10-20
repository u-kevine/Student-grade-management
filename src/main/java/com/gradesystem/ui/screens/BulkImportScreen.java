package com.gradesystem.ui.screens;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.io.CsvRecord;
import com.gradesystem.io.ImportFolder;
import com.gradesystem.service.BulkImportService;
import com.gradesystem.service.ImportResult;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.Screen;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.InputValidator;

import java.util.List;

/**
 * Menu option 7: Bulk Import Grades (US-4).
 */
public class BulkImportScreen implements Screen {

    private final ConsoleIO io;
    private final BulkImportService importService;
    private final ImportFolder importFolder;

    /**
     * Creates a new BulkImportScreen.
     *
     * @param io            console input/output
     * @param importService reads and imports the CSV file
     * @param importFolder  lists the files the user can choose from
     */
    public BulkImportScreen(ConsoleIO io, BulkImportService importService, ImportFolder importFolder) {
        this.io = io;
        this.importService = importService;
        this.importFolder = importFolder;
    }

    @Override
    public String getMenuLabel() {
        return "Bulk Import Grades";
    }

    @Override
    public boolean isNew() {
        return true;
    }

    @Override
    public void show() {
        io.printTitle("BULK IMPORT GRADES");
        io.println("Place your CSV file in: ./" + AppConstants.IMPORTS_DIR + "/");
        io.println();
        io.println("CSV Format Required:");
        io.println(AppConstants.CSV_HEADER);
        io.println("Example: STU001,Mathematics,Core,85");
        io.println();
        showAvailableFiles();
        String fileName = io.readValidText("Enter filename (without extension): ", InputValidator::validateFileName);
        io.println();

        try {
            io.print("Validating file... ");
            List<CsvRecord> records = importService.loadFile(fileName);
            io.println("✓");
            io.println("Processing grades...");
            ImportResult result = importService.importRecords(fileName, records);
            showSummary(result);
        } catch (InvalidInputException | FileImportException | InvalidFileFormatException | FileExportException e) {
            io.showError(e);
        }
        io.pause();
    }

    private void showAvailableFiles() {
        List<String> files = importFolder.listFileNames();
        io.println("Files in ./" + AppConstants.IMPORTS_DIR + "/:");
        if (files.isEmpty()) {
            io.println("  (no CSV files yet)");
        } else {
            for (String file : files) {
                io.println("  - " + file);
            }
        }
        io.println();
    }

    private void showSummary(ImportResult result) {
        io.println();
        io.println("IMPORT SUMMARY");
        io.println(AppConstants.LINE);
        io.println("Total Rows: " + result.getTotalRows());
        io.println("Successfully Imported: " + result.getSuccessCount());
        io.println("Failed: " + result.getFailedCount());
        if (result.getFailedCount() > 0) {
            io.println();
            io.println("Failed Records:");
            for (String failure : result.getFailures()) {
                io.println("  " + failure);
            }
        }
        io.println();
        io.showSuccess("Import completed!");
        io.println("  " + result.getSuccessCount() + " grades added to system");
        io.println("  See " + result.getLogFile() + " for details");
    }
}