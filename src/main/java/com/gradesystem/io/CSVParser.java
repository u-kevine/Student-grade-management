package com.gradesystem.io;

import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;

import java.nio.file.Path;
import java.util.List;

/**
 * Reads a CSV file and returns its data rows.
 *
 * <p>Dependency Inversion: the BulkImportService depends on THIS interface,
 * not on a concrete class. In tests we give it a fake (mock) parser,
 * so no real file is needed.</p>
 */
public interface CSVParser {

    /**
     * Reads the file, checks the header and returns every data row.
     *
     * @param file the CSV file
     * @return the data rows (the header is not included)
     * @throws FileImportException        if the file is missing or cannot be read
     * @throws InvalidFileFormatException if the file is empty, has a wrong header or no data
     */
    List<CsvRecord> parse(Path file) throws FileImportException, InvalidFileFormatException;
}
