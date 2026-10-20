package com.gradesystem.io;

import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Single Responsibility: ONLY reads and splits CSV files.
 * It does not check whether students or grades are valid; that is the import service's job.
 *
 * <p>Expected format:</p>
 * <pre>
 * StudentID,SubjectName,SubjectType,Grade
 * STU001,Mathematics,Core,85
 * </pre>
 */
public class SimpleCSVParser implements CSVParser {

    private static final Logger LOGGER = AppLogger.getLogger(SimpleCSVParser.class);
    /**
     * Creates a CSV parser.
     */
    public SimpleCSVParser() {
        // nothing to set up
    }

    /** Some editors (Excel) put this invisible character at the start of a file. */
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    @Override
    public List<CsvRecord> parse(Path file) throws FileImportException, InvalidFileFormatException {
        String fileName = file.getFileName().toString();
        List<String> lines = readLines(file, fileName);

        if (lines.isEmpty()) {
            throw new InvalidFileFormatException(fileName, "the file is empty.");
        }
        checkHeader(lines.get(0), fileName);

        List<CsvRecord> records = new ArrayList<>();
        int rowNumber = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;   // skip blank lines
            }
            rowNumber++;
            records.add(new CsvRecord(rowNumber, splitLine(line)));
        }

        if (records.isEmpty()) {
            throw new InvalidFileFormatException(fileName, "the file has a header but no data rows.");
        }
        LOGGER.info("Parsed " + records.size() + " rows from " + fileName);
        return records;
    }

    private List<String> readLines(Path file, String fileName) throws FileImportException {
        if (!Files.exists(file)) {
            LOGGER.warning("Import file not found: " + file.toAbsolutePath());
            throw new FileImportException(fileName);
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.severe("Could not read " + fileName + ": " + e.getMessage());
            throw new FileImportException(fileName, e);
        }
    }

    private void checkHeader(String headerLine, String fileName) throws InvalidFileFormatException {
        String header = headerLine.replace(BYTE_ORDER_MARK, "").replace(" ", "");
        if (!header.equalsIgnoreCase(AppConstants.CSV_HEADER)) {
            LOGGER.warning("Wrong header in " + fileName + ": " + headerLine);
            throw new InvalidFileFormatException(fileName, "wrong header '" + headerLine.trim() + "'.");
        }
    }

    /** Splits "STU001, Mathematics ,Core,85" into ["STU001", "Mathematics", "Core", "85"]. */
    private String[] splitLine(String line) {
        String[] parts = line.split(AppConstants.CSV_SEPARATOR, -1);   // -1 keeps empty columns
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
}
