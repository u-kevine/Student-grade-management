package com.gradesystem.io;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.util.AppLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Single Responsibility: ONLY writes text files. It does not know what a report is.
 */
public class TextFileExporter implements FileExporter {

    private static final Logger LOGGER = AppLogger.getLogger(TextFileExporter.class);

    /**
     * Creates a text file exporter.
     */
    public TextFileExporter() {
        // nothing to set up
    }

    @Override
    public Path writeToFile(String directory, String fileName, String content) throws FileExportException {
        Path folder = Paths.get(directory);
        Path file = folder.resolve(fileName);
        try {
            Files.createDirectories(folder);
            Files.writeString(file, content, StandardCharsets.UTF_8);
            LOGGER.info("Wrote file " + file.toAbsolutePath());
            return file;
        } catch (IOException e) {
            LOGGER.severe("Could not write " + file + ": " + e.getMessage());
            throw new FileExportException(fileName, e);
        }
    }
}
