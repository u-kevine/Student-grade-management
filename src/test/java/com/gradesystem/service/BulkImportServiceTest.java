package com.gradesystem.service;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.io.CSVParser;
import com.gradesystem.io.CsvRecord;
import com.gradesystem.io.FileExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * INTEGRATION TEST WITH MOCKING.
 *
 * <p>The CSV parser and the file exporter are mocks (no real files). The
 * StudentManager, GradeManager and SubjectCatalog are real, so we test that
 * the pieces really work together.</p>
 */
class BulkImportServiceTest {

    private CSVParser parser;
    private FileExporter exporter;
    private GradeManager gradeManager;
    private BulkImportService service;

    @BeforeEach
    void setUp() throws Exception {
        parser = mock(CSVParser.class);
        exporter = mock(FileExporter.class);
        when(exporter.writeToFile(anyString(), anyString(), anyString())).thenReturn(Paths.get("logs", "import_log.txt"));

        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(TestData.regular("STU001", "Alice Johnson"));
        studentManager.addStudent(TestData.honors("STU002", "Bob Smith"));
        gradeManager = new GradeManager();

        service = new BulkImportService(parser, studentManager, gradeManager, new SubjectCatalog(),
                exporter, "imports", "logs");
    }

    private static CsvRecord row(int number, String... fields) {
        return new CsvRecord(number, fields);
    }

    @Test
    void importsValidRowsAndReportsEachKindOfBadRow() throws Exception {
        List<CsvRecord> rows = List.of(
                row(1, "STU001", "Mathematics", "Core", "85"),
                row(2, "STU002", "Music", "Elective", "92"),
                row(3, "STU999", "English", "Core", "70"),      // unknown student
                row(4, "STU001", "Science", "Core", "105"),     // grade too high
                row(5, "STU001", "Chemistry", "Core", "80"),    // unknown subject
                row(6, "STU001", "Art", "Elective", "abc"),     // not a number
                row(7, "STU001", "Art"));                       // missing columns
        when(parser.parse(Paths.get("imports", "october.csv"))).thenReturn(rows);

        ImportResult result = service.importGrades("october");

        assertEquals(7, result.getTotalRows());
        assertEquals(2, result.getSuccessCount());
        assertEquals(5, result.getFailedCount());
        assertEquals("Row 3: Invalid student ID (STU999)", result.getFailures().get(0));
        assertEquals("Row 4: Grade out of range or not a number (105)", result.getFailures().get(1));
        assertEquals("Row 5: Invalid subject (Chemistry, Core)", result.getFailures().get(2));
        assertTrue(result.getFailures().get(3).contains("abc"));
        assertEquals("Row 7: Expected 4 columns but found 2", result.getFailures().get(4));
        assertEquals(2, gradeManager.getTotalGradeCount());   // only good rows were stored
        assertEquals(Paths.get("logs", "import_log.txt"), result.getLogFile());
    }

    @Test
    void writesADetailedLogFile() throws Exception {
        when(parser.parse(any(Path.class))).thenReturn(List.of(
                row(1, "STU001", "Mathematics", "Core", "85"),
                row(2, "STU999", "English", "Core", "70")));

        service.importGrades("october");

        ArgumentCaptor<String> fileName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(exporter).writeToFile(eq("logs"), fileName.capture(), content.capture());
        assertTrue(fileName.getValue().startsWith("import_log_"));
        assertTrue(fileName.getValue().endsWith(".txt"));
        assertTrue(content.getValue().contains("Row 1: OK"));
        assertTrue(content.getValue().contains("Row 2: FAILED"));
        assertTrue(content.getValue().contains("Successfully Imported: 1"));
    }

    @Test
    void invalidFileNameStopsBeforeReadingAnything() throws Exception {
        assertThrows(InvalidInputException.class, () -> service.importGrades("../etc/passwd"));
        verify(parser, never()).parse(any(Path.class));
    }

    @Test
    void parserErrorsArePassedToTheCaller() throws Exception {
        when(parser.parse(any(Path.class))).thenThrow(new InvalidFileFormatException("bad.csv", "wrong header"));
        assertThrows(InvalidFileFormatException.class, () -> service.loadFile("bad"));
        verify(exporter, never()).writeToFile(anyString(), anyString(), anyString());
    }

    @Test
    void missingFileIsReported() throws Exception {
        when(parser.parse(any(Path.class))).thenThrow(new FileImportException("missing.csv"));
        assertThrows(FileImportException.class, () -> service.importGrades("missing"));
    }

    @Test
    void logWriteFailureIsReported() throws Exception {
        when(parser.parse(any(Path.class))).thenReturn(List.of(row(1, "STU001", "Mathematics", "Core", "85")));
        when(exporter.writeToFile(anyString(), anyString(), anyString()))
                .thenThrow(new FileExportException("log.txt", new IOException("disk full")));
        assertThrows(FileExportException.class, () -> service.importGrades("october"));
    }

    @Test
    void importResultIsReadOnly() {
        ImportResult result = new ImportResult(3, 2, List.of("Row 3: bad"), Paths.get("log.txt"));
        assertThrows(UnsupportedOperationException.class, () -> result.getFailures().clear());
    }
}
