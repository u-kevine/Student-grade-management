package com.gradesystem.io;

import com.gradesystem.exception.FileExportException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextFileExporterTest {

    @TempDir
    Path tempDir;

    private final FileExporter exporter = new TextFileExporter();

    @Test
    void writesFileAndCreatesMissingFolders() throws Exception {
        String folder = tempDir.resolve("reports/2026").toString();
        Path file = exporter.writeToFile(folder, "report.txt", "Hello ✓");
        assertTrue(Files.exists(file));
        assertEquals("Hello ✓", Files.readString(file));
    }

    @Test
    void overwritesAnExistingFile() throws Exception {
        exporter.writeToFile(tempDir.toString(), "a.txt", "old");
        Path file = exporter.writeToFile(tempDir.toString(), "a.txt", "new");
        assertEquals("new", Files.readString(file));
    }

    @Test
    void failsWithFileExportExceptionWhenFolderIsReallyAFile() throws IOException {
        Path notAFolder = Files.writeString(tempDir.resolve("blocker"), "x");
        FileExportException e = assertThrows(FileExportException.class,
                () -> exporter.writeToFile(notAFolder.toString(), "report.txt", "content"));
        assertNotNull(e.getCause());
    }
}
