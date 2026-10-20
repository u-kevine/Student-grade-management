package com.gradesystem.service;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.FileImportException;
import com.gradesystem.exception.InvalidFileFormatException;
import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.exception.SubjectNotFoundException;
import com.gradesystem.io.CSVParser;
import com.gradesystem.io.CsvRecord;
import com.gradesystem.io.FileExporter;
import com.gradesystem.model.Student;
import com.gradesystem.model.Subject;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;
import com.gradesystem.util.InputValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Imports many grades at once from a CSV file.
 *
 * <p>Dependency Inversion: this high-level class depends on the
 * {@link CSVParser} and {@link FileExporter} INTERFACES. They are passed in
 * through the constructor (dependency injection), so tests can use mocks.</p>
 *
 * <p>Bad rows are skipped and reported; good rows are still imported.</p>
 */
public class BulkImportService {

    private static final Logger LOGGER = AppLogger.getLogger(BulkImportService.class);
    private static final String NEW_LINE = System.lineSeparator();

    private final CSVParser csvParser;
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final SubjectCatalog subjectCatalog;
    private final FileExporter fileExporter;
    private final String importsDirectory;
    private final String logsDirectory;

    /**
     * Creates a new BulkImportService.
     *
     * @param csvParser        reads the CSV file
     * @param studentManager   checks that students exist
     * @param gradeManager     stores the imported grades
     * @param subjectCatalog   checks that subjects exist
     * @param fileExporter     writes the import log
     * @param importsDirectory folder with CSV files, normally "imports"
     * @param logsDirectory    folder for the import log, normally "logs"
     */
    public BulkImportService(CSVParser csvParser, StudentManager studentManager, GradeManager gradeManager,
                             SubjectCatalog subjectCatalog, FileExporter fileExporter,
                             String importsDirectory, String logsDirectory) {
        this.csvParser = csvParser;
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.subjectCatalog = subjectCatalog;
        this.fileExporter = fileExporter;
        this.importsDirectory = importsDirectory;
        this.logsDirectory = logsDirectory;
    }

    /**
     * Step 1: reads the file and checks its format. Nothing is imported yet.
     *
     * @param fileName file name WITHOUT .csv
     * @return the data rows
     * @throws InvalidInputException      if the file name is not valid
     * @throws FileImportException        if the file is missing or unreadable
     * @throws InvalidFileFormatException if the header or layout is wrong
     */
    public List<CsvRecord> loadFile(String fileName)
            throws InvalidInputException, FileImportException, InvalidFileFormatException {
        InputValidator.validateFileName(fileName);
        Path file = Paths.get(importsDirectory, fileName + AppConstants.CSV_EXTENSION);
        return csvParser.parse(file);
    }

    /**
     * Step 2: imports every valid row, skips bad rows and writes a log file.
     *
     * @param fileName the file name (used in the log)
     * @param records  the rows from {@link #loadFile(String)}
     * @return the summary of the import
     * @throws FileExportException if the log file cannot be written
     */
    public ImportResult importRecords(String fileName, List<CsvRecord> records) throws FileExportException {
        List<String> failures = new ArrayList<>();
        StringBuilder log = new StringBuilder();
        log.append("IMPORT LOG - ").append(fileName).append(AppConstants.CSV_EXTENSION).append(NEW_LINE);
        log.append("Started: ").append(LocalDateTime.now()).append(NEW_LINE).append(NEW_LINE);

        int successCount = 0;
        for (CsvRecord record : records) {
            String error = importRow(record);
            if (error == null) {
                successCount++;
                log.append("Row ").append(record.getRowNumber()).append(": OK      ").append(record.toLine()).append(NEW_LINE);
            } else {
                String message = "Row " + record.getRowNumber() + ": " + error;
                failures.add(message);
                log.append("Row ").append(record.getRowNumber()).append(": FAILED  ").append(record.toLine())
                        .append("  -> ").append(error).append(NEW_LINE);
                LOGGER.warning("Import " + fileName + " " + message);
            }
        }

        log.append(NEW_LINE).append("Total Rows: ").append(records.size()).append(NEW_LINE);
        log.append("Successfully Imported: ").append(successCount).append(NEW_LINE);
        log.append("Failed: ").append(failures.size()).append(NEW_LINE);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(AppConstants.FILE_TIMESTAMP_PATTERN));
        String logName = AppConstants.IMPORT_LOG_PREFIX + timestamp + AppConstants.TEXT_EXTENSION;
        Path logFile = fileExporter.writeToFile(logsDirectory, logName, log.toString());
        LOGGER.info("Import of " + fileName + " finished: " + successCount + " ok, " + failures.size() + " failed");
        return new ImportResult(records.size(), successCount, failures, logFile);
    }

    /**
     * Steps 1 and 2 together.
     *
     * @param fileName file name WITHOUT .csv
     * @return the summary of the import
     * @throws InvalidInputException      if the file name is not valid
     * @throws FileImportException        if the file is missing or unreadable
     * @throws InvalidFileFormatException if the header or layout is wrong
     * @throws FileExportException        if the log file cannot be written
     */
    public ImportResult importGrades(String fileName)
            throws InvalidInputException, FileImportException, InvalidFileFormatException, FileExportException {
        return importRecords(fileName, loadFile(fileName));
    }

    /**
     * Imports one row.
     *
     * @return null when the row worked, otherwise a short reason why it failed
     */
    private String importRow(CsvRecord record) {
        if (record.getFieldCount() != AppConstants.CSV_COLUMN_COUNT) {
            return "Expected " + AppConstants.CSV_COLUMN_COUNT + " columns but found " + record.getFieldCount();
        }
        String studentId = record.getField(0);
        String subjectName = record.getField(1);
        String subjectType = record.getField(2);
        String gradeText = record.getField(3);
        try {
            Student student = studentManager.findStudent(studentId);
            Subject subject = subjectCatalog.findSubject(subjectName, subjectType);
            double value = InputValidator.parseGrade(gradeText);
            gradeManager.recordGrade(student.getStudentId(), subject, value);
            return null;
        } catch (StudentNotFoundException e) {
            return "Invalid student ID (" + studentId + ")";
        } catch (SubjectNotFoundException e) {
            return "Invalid subject (" + subjectName + ", " + subjectType + ")";
        } catch (InvalidGradeException e) {
            return "Grade out of range or not a number (" + e.getEnteredValue() + ")";
        }
    }
}
