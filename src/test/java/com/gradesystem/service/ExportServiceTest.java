package com.gradesystem.service;

import com.gradesystem.calculator.FourPointGradingScale;
import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.io.FileExporter;
import com.gradesystem.model.CoreSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * INTEGRATION TEST WITH MOCKING: the FileExporter is a mock, so no file is written.
 */
class ExportServiceTest {

    private FileExporter exporter;
    private ExportService service;
    private StudentManager studentManager;

    @BeforeEach
    void setUp() throws Exception {
        exporter = mock(FileExporter.class);
        when(exporter.writeToFile(anyString(), anyString(), anyString()))
                .thenReturn(Paths.get("reports", "alice.txt"));

        studentManager = new StudentManager();
        studentManager.addStudent(TestData.regular("STU001", "Alice Johnson"));
        studentManager.addStudent(TestData.honors("STU002", "Bob Smith"));
        GradeManager gradeManager = new GradeManager();
        gradeManager.recordGrade("STU001", new CoreSubject("Mathematics", "MTH101"), 88);

        ReportGenerator generator = new ReportGenerator(gradeManager, new GPACalculator(new FourPointGradingScale()));
        service = new ExportService(studentManager, generator, exporter, "reports");
    }

    @Test
    void exportsReportToReportsFolderWithTxtExtension() throws Exception {
        ExportResult result = service.exportStudentReport("STU001", ReportType.DETAILED, "alice");

        ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(exporter).writeToFile(eq("reports"), eq("alice.txt"), content.capture());
        assertTrue(content.getValue().contains("Alice Johnson"));
        assertTrue(content.getValue().contains("GRADE HISTORY"));
        assertEquals(content.getValue().getBytes(StandardCharsets.UTF_8).length, result.getSizeInBytes());
        assertEquals("alice.txt", result.getFileName());
        assertEquals("./reports/", result.getLocation());
        assertTrue(result.getFormattedSize().endsWith(" KB"));
    }

    @Test
    void invalidFileNameNeverTouchesTheDisk() throws Exception {
        assertThrows(InvalidInputException.class,
                () -> service.exportStudentReport("STU001", ReportType.SUMMARY, "bad/name"));
        verify(exporter, never()).writeToFile(anyString(), anyString(), anyString());
    }

    @Test
    void unknownStudentAndStudentWithoutGradesThrow() {
        assertThrows(StudentNotFoundException.class,
                () -> service.exportStudentReport("STU999", ReportType.SUMMARY, "x"));
        assertThrows(NoGradesException.class,
                () -> service.exportStudentReport("STU002", ReportType.SUMMARY, "x"));
    }

    @Test
    void writeFailureBecomesFileExportException() throws Exception {
        when(exporter.writeToFile(anyString(), anyString(), anyString()))
                .thenThrow(new FileExportException("alice.txt", new IOException("disk full")));
        assertThrows(FileExportException.class,
                () -> service.exportStudentReport("STU001", ReportType.SUMMARY, "alice"));
    }

    @Test
    void exportsSearchResults() throws Exception {
        service.exportSearchResults(studentManager.getAllStudents(), "Type = Any", "results");
        ArgumentCaptor<String> content = ArgumentCaptor.forClass(String.class);
        verify(exporter).writeToFile(eq("reports"), eq("results.txt"), content.capture());
        assertTrue(content.getValue().contains("Found: 2 student(s)"));
    }

    @Test
    void exportResultLocationWithoutFolder() {
        ExportResult result = new ExportResult(Paths.get("file.txt"), 2048);
        assertEquals("./", result.getLocation());
        assertEquals(String.format("%.1f KB", 2.0), result.getFormattedSize());
        assertEquals(Paths.get("file.txt"), result.getFilePath());
    }
}
