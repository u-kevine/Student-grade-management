package com.gradesystem.io;

import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Uses JUnit's @TempDir: a fresh, empty folder for every test that is deleted afterwards.
 */
class SimpleCSVParserTest {

    @TempDir
    Path tempDir;

    private final CSVParser parser = new SimpleCSVParser();

    private Path write(String content) throws IOException {
        Path file = tempDir.resolve("grades.csv");
        Files.writeString(file, content);
        return file;
    }

    @Test
    void readsRowsAndTrimsValues() throws Exception {
        Path file = write("StudentID,SubjectName,SubjectType,Grade\nSTU001, Mathematics ,Core,85\nSTU002,Music,Elective,78\n");
        List<CsvRecord> rows = parser.parse(file);
        assertEquals(2, rows.size());
        assertEquals(1, rows.get(0).getRowNumber());
        assertEquals("Mathematics", rows.get(0).getField(1));
        assertEquals(4, rows.get(1).getFieldCount());
        assertEquals("STU002,Music,Elective,78", rows.get(1).toLine());
    }

    @Test
    void skipsBlankLinesAndAcceptsHeaderWithSpacesOrBom() throws Exception {
        Path file = write("\uFEFFStudentID, SubjectName, SubjectType, Grade\n\nSTU001,Art,Elective,90\n   \n");
        List<CsvRecord> rows = parser.parse(file);
        assertEquals(1, rows.size());
    }

    @Test
    void keepsEmptyColumnsSoTheServiceCanReportThem() throws Exception {
        Path file = write("StudentID,SubjectName,SubjectType,Grade\nSTU001,,Core,\n");
        assertEquals(4, parser.parse(file).get(0).getFieldCount());
    }

    @Test
    void missingFileThrowsFileImportException() {
        assertThrows(FileImportException.class, () -> parser.parse(tempDir.resolve("nope.csv")));
    }

    @Test
    void unreadableFileThrowsFileImportExceptionWithCause() throws IOException {
        Path folder = Files.createDirectory(tempDir.resolve("folder.csv"));   // a folder cannot be read as text
        FileImportException e = assertThrows(FileImportException.class, () -> parser.parse(folder));
        assertTrue(e.getCause() instanceof IOException);
    }

    @Test
    void emptyFileIsInvalidFormat() throws IOException {
        Path file = write("");
        InvalidFileFormatException e = assertThrows(InvalidFileFormatException.class, () -> parser.parse(file));
        assertTrue(e.getMessage().contains("empty"));
    }

    @Test
    void wrongHeaderIsInvalidFormat() throws IOException {
        Path file = write("Name,Course,Score\nAlice,Math,85\n");
        InvalidFileFormatException e = assertThrows(InvalidFileFormatException.class, () -> parser.parse(file));
        assertTrue(e.getMessage().contains("wrong header"));
    }

    @Test
    void headerWithoutRowsIsInvalidFormat() throws IOException {
        Path file = write("StudentID,SubjectName,SubjectType,Grade\n");
        InvalidFileFormatException e = assertThrows(InvalidFileFormatException.class, () -> parser.parse(file));
        assertTrue(e.getMessage().contains("no data rows"));
    }
}
