package com.gradesystem.service;

import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.NoGradesException;
import com.gradesystem.exception.StudentNotFoundException;
import com.gradesystem.io.FileExporter;
import com.gradesystem.model.Student;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.InputValidator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Exports reports to text files.
 *
 * <p>It joins two small classes together: {@link ReportGenerator} builds the
 * text and {@link FileExporter} writes it. Both are injected, so in tests the
 * FileExporter can be a mock and no real files are written.</p>
 */
public class ExportService {

    private final StudentManager studentManager;
    private final ReportGenerator reportGenerator;
    private final FileExporter fileExporter;
    private final String reportsDirectory;

    /**
     * Creates a new ExportService.
     *
     * @param studentManager   where the students are stored
     * @param reportGenerator  builds the report text
     * @param fileExporter     writes files
     * @param reportsDirectory folder for reports, normally "reports"
     */
    public ExportService(StudentManager studentManager, ReportGenerator reportGenerator,
                         FileExporter fileExporter, String reportsDirectory) {
        this.studentManager = studentManager;
        this.reportGenerator = reportGenerator;
        this.fileExporter = fileExporter;
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Exports one student's report.
     *
     * @param studentId the student
     * @param type      summary, detailed or both
     * @param fileName  file name WITHOUT extension
     * @return where the file was saved and how big it is
     * @throws StudentNotFoundException if the student does not exist
     * @throws NoGradesException        if the student has no grades
     * @throws InvalidInputException    if the file name is not valid
     * @throws FileExportException      if writing the file fails
     */
    public ExportResult exportStudentReport(String studentId, ReportType type, String fileName)
            throws StudentNotFoundException, NoGradesException, InvalidInputException, FileExportException {
        InputValidator.validateFileName(fileName);
        Student student = studentManager.findStudent(studentId);
        String content = reportGenerator.generateReport(student, type);
        return save(fileName, content);
    }

    /**
     * Exports a list of search results.
     *
     * @param students the students found
     * @param criteria a description of the search
     * @param fileName file name WITHOUT extension
     * @return where the file was saved and how big it is
     * @throws InvalidInputException if the file name is not valid
     * @throws FileExportException   if writing the file fails
     */
    public ExportResult exportSearchResults(List<Student> students, String criteria, String fileName)
            throws InvalidInputException, FileExportException {
        InputValidator.validateFileName(fileName);
        String content = reportGenerator.generateSearchReport(students, criteria);
        return save(fileName, content);
    }

    private ExportResult save(String fileName, String content) throws FileExportException {
        Path path = fileExporter.writeToFile(reportsDirectory, fileName + AppConstants.TEXT_EXTENSION, content);
        long size = content.getBytes(StandardCharsets.UTF_8).length;
        return new ExportResult(path, size);
    }
}
