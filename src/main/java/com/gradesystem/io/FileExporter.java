package com.gradesystem.io;

import com.gradesystem.exception.FileExportException;

import java.nio.file.Path;

/**
 * Writes text to a file.
 *
 * <p>Dependency Inversion: services depend on this interface. In tests we
 * use a mock, so no files are created on disk.</p>
 */
public interface FileExporter {

    /**
     * Writes text to {@code directory/fileName}, creating the folder if needed.
     * An existing file with the same name is replaced.
     *
     * @param directory the folder, for example "reports"
     * @param fileName  the file name WITH extension, for example "alice.txt"
     * @param content   the text to write
     * @return the path of the written file
     * @throws FileExportException if the file cannot be written
     */
    Path writeToFile(String directory, String fileName, String content) throws FileExportException;
}
