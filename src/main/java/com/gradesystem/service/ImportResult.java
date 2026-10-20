package com.gradesystem.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The outcome of a bulk import: how many rows worked, which ones failed and why.
 */
public class ImportResult {

    private final int totalRows;
    private final int successCount;
    private final List<String> failures;
    private final Path logFile;

    /**
     * Creates a new ImportResult.
     *
     * @param totalRows    number of data rows in the file
     * @param successCount rows that were imported
     * @param failures     one message per failed row, for example "Row 7: Invalid student ID (STU999)"
     * @param logFile      the import log that was written
     */
    public ImportResult(int totalRows, int successCount, List<String> failures, Path logFile) {
        this.totalRows = totalRows;
        this.successCount = successCount;
        this.failures = new ArrayList<>(failures);
        this.logFile = logFile;
    }

    /** {@return number of data rows in the file} */
    public int getTotalRows() { return totalRows; }

    /** {@return rows that were imported} */
    public int getSuccessCount() { return successCount; }

    /** {@return rows that failed} */
    public int getFailedCount() { return failures.size(); }

    /** {@return one message per failed row (read-only)} */
    public List<String> getFailures() { return Collections.unmodifiableList(failures); }

    /** {@return the import log file} */
    public Path getLogFile() { return logFile; }
}
