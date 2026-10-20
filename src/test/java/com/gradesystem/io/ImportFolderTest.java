package com.gradesystem.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ImportFolderTest {

    @TempDir
    Path tempDir;

    @Test
    void prepareCreatesFolderAndStarterFile() throws Exception {
        String folder = tempDir.resolve("imports").toString();
        new ImportFolder(folder, new TextFileExporter()).prepare();

        Path starter = tempDir.resolve("imports").resolve("grades.csv");
        assertTrue(Files.exists(starter));
        List<String> lines = Files.readAllLines(starter);
        assertEquals("StudentID,SubjectName,SubjectType,Grade", lines.get(0));
        assertEquals(6, lines.size());   // header + 5 grades
    }

    @Test
    void prepareNeverOverwritesYourOwnEdits() throws Exception {
        Path starter = Files.writeString(tempDir.resolve("grades.csv"), "my own content");
        FileExporter exporter = mock(FileExporter.class);

        new ImportFolder(tempDir.toString(), exporter).prepare();

        verify(exporter, never()).writeToFile(anyString(), anyString(), anyString());
        assertEquals("my own content", Files.readString(starter));
    }

    @Test
    void listsCsvFilesSortedAndWithoutExtension() throws IOException {
        Files.writeString(tempDir.resolve("october.csv"), "x");
        Files.writeString(tempDir.resolve("april.csv"), "x");
        Files.writeString(tempDir.resolve("notes.txt"), "x");   // not a CSV, so not listed

        List<String> names = new ImportFolder(tempDir.toString(), new TextFileExporter()).listFileNames();

        assertEquals(List.of("april", "october"), names);
    }

    @Test
    void missingFolderGivesEmptyList() {
        ImportFolder folder = new ImportFolder(tempDir.resolve("nope").toString(), new TextFileExporter());
        assertTrue(folder.listFileNames().isEmpty());
    }
}