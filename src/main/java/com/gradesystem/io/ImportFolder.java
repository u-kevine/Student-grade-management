package com.gradesystem.io;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Looks after the {@code imports/} folder that bulk import reads from.
 *
 * <p>It does two jobs:</p>
 * <ul>
 *   <li>{@link #prepare()} makes sure the folder exists and contains a ready-to-use
 *       file called {@code grades.csv}, every time the application starts.</li>
 *   <li>{@link #listFileNames()} lists the CSV files in the folder, so the import
 *       screen can show the user which names they can type.</li>
 * </ul>
 */
public class ImportFolder {

    private static final Logger LOGGER = AppLogger.getLogger(ImportFolder.class);
    private static final String NEW_LINE = System.lineSeparator();

    /** What the starter file contains. Add students STU001 and STU002 first, then import it. */
    private static final String STARTER_CONTENT = String.join(NEW_LINE,
            AppConstants.CSV_HEADER,
            "STU001,Mathematics,Core,85",
            "STU001,English,Core,78",
            "STU001,Music,Elective,92",
            "STU002,Science,Core,88",
            "STU002,Art,Elective,74") + NEW_LINE;

    private final String directory;
    private final FileExporter fileExporter;

    /**
     * Creates a new ImportFolder.
     *
     * @param directory    the folder, normally "imports"
     * @param fileExporter used to write the starter file (so writing files stays in one class)
     */
    public ImportFolder(String directory, FileExporter fileExporter) {
        this.directory = directory;
        this.fileExporter = fileExporter;
    }

    /**
     * Creates the folder and the starter file if they are missing.
     * An existing starter file is never overwritten, so your own edits are kept.
     *
     * @throws FileExportException if the starter file cannot be written
     */
    public void prepare() throws FileExportException {
        String fileName = AppConstants.STARTER_IMPORT_FILE + AppConstants.CSV_EXTENSION;
        if (!Files.exists(Paths.get(directory, fileName))) {
            fileExporter.writeToFile(directory, fileName, STARTER_CONTENT);
            LOGGER.info("Created starter import file " + fileName);
        }
    }

    /**
     * Lists the CSV files in the folder, sorted by name.
     *
     * @return the file names WITHOUT ".csv", for example [grades, october_grades]
     */
    public List<String> listFileNames() {
        List<String> names = new ArrayList<>();
        Path folder = Paths.get(directory);
        if (!Files.isDirectory(folder)) {
            return names;
        }
        try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*" + AppConstants.CSV_EXTENSION)) {
            for (Path file : files) {
                String name = file.getFileName().toString();
                names.add(name.substring(0, name.length() - AppConstants.CSV_EXTENSION.length()));
            }
        } catch (IOException e) {
            // Listing is only a convenience, so we log the problem and show an empty list.
            LOGGER.warning("Could not list " + folder + ": " + e.getMessage());
        }
        Collections.sort(names);
        return names;
    }
}