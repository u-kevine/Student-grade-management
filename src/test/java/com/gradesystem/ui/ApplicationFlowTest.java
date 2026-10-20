package com.gradesystem.ui;

import com.gradesystem.Main;
import com.gradesystem.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * END-TO-END TESTS: starts the REAL application (exactly like Main does)
 * and "types" menu choices into it, then checks what was printed.
 *
 * <p>Each line in a script is one thing the user types. An empty string ""
 * is pressing Enter at a "Press Enter to continue..." prompt.</p>
 */
class ApplicationFlowTest {

    private static final String TEST_REPORT = "flow_test_report";
    private static final String TEST_SEARCH = "flow_test_search";

    /** Runs the app (no students, like the real program) and returns everything it printed. */
    private String run(String... lines) {
        return start(false, lines);
    }

    /** Same as run(), but starts with the 5 sample students STU001-STU005 and their grades. */
    private String runWithSampleData(String... lines) {
        return start(true, lines);
    }

    private String start(boolean sampleData, String... lines) {
        String input = String.join("\n", lines) + "\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output, true, StandardCharsets.UTF_8);
        ConsoleIO io = new ConsoleIO(new Scanner(input), out);
        Main.createMenu(io, sampleData).run();
        return output.toString(StandardCharsets.UTF_8);
    }

    @AfterEach
    void deleteTestReports() throws IOException {
        Files.deleteIfExists(Paths.get(AppConstants.REPORTS_DIR, TEST_REPORT + AppConstants.TEXT_EXTENSION));
        Files.deleteIfExists(Paths.get(AppConstants.REPORTS_DIR, TEST_SEARCH + AppConstants.TEXT_EXTENSION));
    }

    @Test
    void menuShowsAllTenOptionsAndExits() {
        String out = run("10");
        assertFalse(out.contains("Load sample data"));   // goes straight to the menu
        assertTrue(out.contains("5. Export Grade Report"));
        assertTrue(out.contains("[NEW]"));
        assertTrue(out.contains("10. Exit"));
        assertTrue(out.contains("Goodbye!"));
    }

    @Test
    void addStudentWithValidationThenViewStudents() {
        String out = run(
                "1", "R2D2", "Alice Johnson", "16", "alice", "alice@school.edu", "12", "0788123456", "2", "",
                "2", "",
                "10");
        assertTrue(out.contains("Name can only contain letters"));
        assertTrue(out.contains("is not a valid email"));
        assertTrue(out.contains("is not a valid phone number"));
        assertTrue(out.contains("✓ Student added successfully!"));
        assertTrue(out.contains("Student ID: STU001"));
        assertTrue(out.contains("Type: Honors Student"));
        assertTrue(out.contains("No Grades"));
        assertTrue(out.contains("Total Students: 1"));
    }

    @Test
    void viewStudentsWithSampleDataShowsAveragesAndHonors() {
        String out = runWithSampleData("3", "STU003", "1", "1", "0", "", "3", "STU003", "1", "1", "0", "", "2", "", "10");
        assertTrue(out.contains("Total Students: 5"));
        assertTrue(out.contains("Honors Eligible"));
        assertTrue(out.contains("Failing"));          // two zeros pull Carol below 50%
        assertTrue(out.contains("Average Class Grade:"));
    }

    @Test
    void emptySystemScreensExplainWhatToDo() {
        String out = run("2", "", "8", "", "3", "STU001", "N", "", "10");
        assertTrue(out.contains("No students yet."));
        assertTrue(out.contains("No grades recorded yet."));
        assertTrue(out.contains("No students have been added yet."));
    }

    @Test
    void recordGradeShowsBothExceptionsFromTheBrief() {
        String out = runWithSampleData(
                "3", "STU999", "Y", "STU001", "1", "1", "150", "Y", "95", "",
                "3", "STU002", "2", "1", "-5", "N", "",
                "10");
        assertTrue(out.contains("✗ ERROR: StudentNotFoundException"));
        assertTrue(out.contains("Available student IDs: STU001, STU002, STU003, STU004, STU005"));
        assertTrue(out.contains("✗ ERROR: InvalidGradeException"));
        assertTrue(out.contains("You entered: 150"));
        assertTrue(out.contains("✓ Grade recorded successfully!"));
        assertTrue(out.contains("Grade cancelled. Nothing was saved."));
    }

    @Test
    void viewReportAndStudentWithoutGrades() {
        String out = runWithSampleData(
                "4", "STU002", "",
                "1", "New Kid", "15", "new@school.edu", "0788000009", "1", "",
                "4", "STU006", "",
                "4", "STU404", "N", "",
                "10");
        assertTrue(out.contains("STUDENT GRADE REPORT - DETAILED REPORT"));
        assertTrue(out.contains("Bob Johnson"));
        assertTrue(out.contains("✗ ERROR: NoGradesException"));
    }

    @Test
    void exportReportWritesFileInReportsFolder() {
        String out = runWithSampleData("5", "STU001", "2", "bad name", TEST_REPORT, "", "5", "STU999", "N", "", "10");
        assertTrue(out.contains("✓ Report exported successfully!"));
        assertTrue(out.contains("File: " + TEST_REPORT + ".txt"));
        assertTrue(out.contains("Location: ./reports/"));
        assertTrue(out.contains("Contains: 5 grades"));
        assertTrue(Files.exists(Paths.get("reports", TEST_REPORT + ".txt")));
    }

    @Test
    void exportForStudentWithoutGradesShowsError() {
        String out = run("1", "Solo Student", "15", "solo@school.edu", "0788000009", "1", "",
                "5", "STU001", "1", TEST_REPORT, "", "10");
        assertTrue(out.contains("✗ ERROR: NoGradesException"));
        assertFalse(Files.exists(Paths.get("reports", TEST_REPORT + ".txt")));
    }

    @Test
    void gpaScreenShowsBreakdownRankAndAnalysis() {
        String out = runWithSampleData("6", "STU002", "", "6", "STU003", "", "6", "STU004", "", "6", "STU999", "N", "", "10");
        assertTrue(out.contains("GPA CALCULATION (4.0 Scale)"));
        assertTrue(out.contains("Mathematics        | 92%    | 3.7 (A-)"));
        assertTrue(out.contains("Cumulative GPA: 3.28 / 4.0"));
        assertTrue(out.contains("Class Rank: 2 of 5"));
        assertTrue(out.contains("✓ Honors eligibility maintained"));
        assertTrue(out.contains("✓ Above class average"));
        assertTrue(out.contains("Needs improvement"));           // STU003
        assertTrue(out.contains("• Below class average"));      // STU003
        assertTrue(out.contains("Excellent performance"));       // STU004
    }

    @Test
    void gpaScreenForStudentWithoutGradesAndHonorsAtRisk() {
        String out = run(
                "1", "Hana Mori", "16", "hana@school.edu", "0788000001", "2", "",
                "6", "STU001", "",
                "3", "STU001", "1", "1", "75", "",
                "6", "STU001", "",
                "10");
        assertTrue(out.contains("✗ ERROR: NoGradesException"));
        assertTrue(out.contains("Honors eligibility at risk"));
        assertTrue(out.contains("Satisfactory performance"));
        assertTrue(out.contains("• Equal to class average"));
    }

    @Test
    void bulkImportOfSampleFileGivesThirteenOkAndTwoFailed() {
        String out = runWithSampleData("7", "sample_grades", "", "10");
        assertTrue(out.contains("Validating file... ✓"));
        assertTrue(out.contains("Total Rows: 15"));
        assertTrue(out.contains("Successfully Imported: 13"));
        assertTrue(out.contains("Row 7: Invalid student ID (STU999)"));
        assertTrue(out.contains("Row 12: Grade out of range or not a number (105)"));
        assertTrue(out.contains("✓ Import completed!"));
    }

    @Test
    void bulkImportListsFilesAndImportsTheStarterFile() {
        String out = run(
                "1", "Alice Johnson", "16", "alice@school.edu", "0788000001", "1", "",
                "1", "Bob Smith", "17", "bob@school.edu", "0788000002", "2", "",
                "7", "grades", "",
                "10");
        assertTrue(out.contains("Files in ./imports/:"));
        assertTrue(out.contains("  - grades"));
        assertTrue(out.contains("  - sample_grades"));
        assertTrue(out.contains("Total Rows: 5"));
        assertTrue(out.contains("Successfully Imported: 5"));
    }

    @Test
    void bulkImportErrors() {
        String out = runWithSampleData("7", "invalid_format", "", "7", "does_not_exist", "", "10");
        assertTrue(out.contains("✗ ERROR: InvalidFileFormatException"));
        assertTrue(out.contains("✗ ERROR: FileImportException"));
    }

    @Test
    void statisticsScreenShowsEverySection() {
        String out = runWithSampleData("8", "", "10");
        assertTrue(out.contains("Total Grades Recorded: 25"));
        assertTrue(out.contains("GRADE DISTRIBUTION"));
        assertTrue(out.contains("(1 grade)"));
        assertTrue(out.contains("Mode:                85.0%"));
        assertTrue(out.contains("Highest Grade:  100% (STU004 - Science)"));
        assertTrue(out.contains("Lowest Grade:    42% (STU003 - Mathematics)"));
        assertTrue(out.contains("Core Subjects:"));
        assertTrue(out.contains("Honors Students:"));
    }

    @Test
    void statisticsWithoutModeAndMissingSubjects() {
        String out = run("1", "Only One", "15", "one@school.edu", "0788000001", "1", "",
                "3", "STU001", "1", "1", "88", "", "8", "", "10");
        assertTrue(out.contains("None (no grade repeats)"));
        assertTrue(out.contains("N/A"));
    }

    @Test
    void searchByNameThenViewDetailsAndExport() {
        String out = runWithSampleData("9", "2", "john", "1", "STU002", "1", "STU005", "2", TEST_SEARCH, "4", "10");
        assertTrue(out.contains("SEARCH RESULTS (2 found)"));
        assertTrue(out.contains("Alice Johnson"));
        assertTrue(out.contains("Bob Johnson"));
        assertTrue(out.contains("Honors Eligible: Yes"));
        assertTrue(out.contains("'STU005' is not in these search results."));
        assertTrue(out.contains("✓ Search results exported"));
        assertTrue(Files.exists(Paths.get("reports", TEST_SEARCH + ".txt")));
    }

    @Test
    void searchByIdRangeAndType() {
        String out = runWithSampleData(
                "9", "1", "stu003", "3",
                "3", "80", "90", "3",
                "4", "2", "3",
                "1", "STU777", "1", "4",
                "10");
        assertTrue(out.contains("Carol Martinez"));
        assertTrue(out.contains("SEARCH RESULTS (2 found)"));   // honors
        assertTrue(out.contains("SEARCH RESULTS (0 found)"));
        assertTrue(out.contains("There are no results to view."));
    }

    @Test
    void searchWithInvalidRangeOffersToSearchAgain() {
        String out = runWithSampleData("9", "3", "90", "80", "Y", "2", "  ", "x", "4", "10");
        assertTrue(out.contains("The minimum cannot be bigger than the maximum."));
        assertTrue(out.contains("SEARCH RESULTS (0 found)"));
    }

    @Test
    void closingInputEndsTheProgramPolitely() {
        String out = run("1", "Alice");   // input ends in the middle of Add Student
        assertTrue(out.contains("Input closed."));
        assertTrue(out.contains("Goodbye!"));
    }

    @Test
    void reportExportedByTheAppIsReadable() throws IOException {
        runWithSampleData("5", "STU004", "3", TEST_REPORT, "", "10");
        Path file = Paths.get("reports", TEST_REPORT + ".txt");
        String content = Files.readString(file);
        assertTrue(content.contains("David Lee"));
        assertTrue(content.contains("SUMMARY + DETAILED"));
        assertTrue(content.contains("Honors: Eligible"));
    }
}