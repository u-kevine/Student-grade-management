package com.gradesystem;

import com.gradesystem.calculator.FourPointGradingScale;
import com.gradesystem.calculator.GPACalculator;
import com.gradesystem.calculator.StatisticsCalculator;
import com.gradesystem.exception.FileExportException;
import com.gradesystem.exception.InvalidGradeException;
import com.gradesystem.exception.InvalidInputException;
import com.gradesystem.exception.SubjectNotFoundException;
import com.gradesystem.io.FileExporter;
import com.gradesystem.io.ImportFolder;
import com.gradesystem.io.SimpleCSVParser;
import com.gradesystem.io.TextFileExporter;
import com.gradesystem.service.BulkImportService;
import com.gradesystem.service.ExportService;
import com.gradesystem.service.GPAService;
import com.gradesystem.service.GradeManager;
import com.gradesystem.service.ReportGenerator;
import com.gradesystem.service.SampleDataLoader;
import com.gradesystem.service.SearchService;
import com.gradesystem.service.StatisticsService;
import com.gradesystem.service.StudentManager;
import com.gradesystem.service.SubjectCatalog;
import com.gradesystem.ui.ConsoleIO;
import com.gradesystem.ui.ConsoleMenu;
import com.gradesystem.ui.Screen;
import com.gradesystem.ui.StudentDetailsView;
import com.gradesystem.ui.StudentLookup;
import com.gradesystem.ui.screens.AddStudentScreen;
import com.gradesystem.ui.screens.BulkImportScreen;
import com.gradesystem.ui.screens.ExportReportScreen;
import com.gradesystem.ui.screens.GpaScreen;
import com.gradesystem.ui.screens.GradeReportScreen;
import com.gradesystem.ui.screens.RecordGradeScreen;
import com.gradesystem.ui.screens.SearchScreen;
import com.gradesystem.ui.screens.StatisticsScreen;
import com.gradesystem.ui.screens.ViewStudentsScreen;
import com.gradesystem.util.AppConstants;
import com.gradesystem.util.AppLogger;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Starts the application.
 *
 * <p>This is the ONLY place where objects are created with {@code new} and
 * connected to each other. Every other class just receives what it needs
 * through its constructor. This is called <b>dependency injection</b>, and
 * {@code Main} is the "composition root".</p>
 */
public final class Main {

    private static final Logger LOGGER = AppLogger.getLogger(Main.class);

    private Main() {
        // only the static methods are used
    }

    /**
     * Program entry point.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the Student Grade Management System!");
        ConsoleIO io = new ConsoleIO(new Scanner(System.in), System.out);
        LOGGER.info("Application started");
        createMenu(io).run();
        LOGGER.info("Application stopped");
    }

    /**
     * Builds the application with NO students: every student is added by hand.
     *
     * @param io console input/output to use
     * @return the ready-to-run main menu
     */
    public static ConsoleMenu createMenu(ConsoleIO io) {
        return createMenu(io, false);
    }

    /**
     * Builds every object and connects them. Public so tests can start the
     * real application with scripted input.
     *
     * @param io             console input/output to use
     * @param loadSampleData true to start with 5 sample students (only used by the tests)
     * @return the ready-to-run main menu
     */
    public static ConsoleMenu createMenu(ConsoleIO io, boolean loadSampleData) {
        // 1. Data storage
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();
        SubjectCatalog subjectCatalog = new SubjectCatalog();

        // 2. Calculators (swap FourPointGradingScale for another scale to change GPA rules)
        GPACalculator gpaCalculator = new GPACalculator(new FourPointGradingScale());
        StatisticsCalculator statisticsCalculator = new StatisticsCalculator();

        // 3. File handling (interfaces on the left, real classes on the right)
        FileExporter fileExporter = new TextFileExporter();

        // 4. Services
        ReportGenerator reportGenerator = new ReportGenerator(gradeManager, gpaCalculator);
        ExportService exportService = new ExportService(studentManager, reportGenerator, fileExporter,
                AppConstants.REPORTS_DIR);
        GPAService gpaService = new GPAService(studentManager, gradeManager, gpaCalculator);
        StatisticsService statisticsService = new StatisticsService(studentManager, gradeManager, statisticsCalculator);
        SearchService searchService = new SearchService(studentManager, gradeManager);
        BulkImportService importService = new BulkImportService(new SimpleCSVParser(), studentManager,
                gradeManager, subjectCatalog, fileExporter, AppConstants.IMPORTS_DIR, AppConstants.LOGS_DIR);

        // 5. Make sure ./imports/ exists and holds the ready-to-use grades.csv
        ImportFolder importFolder = new ImportFolder(AppConstants.IMPORTS_DIR, fileExporter);
        prepareImportFolder(io, importFolder);

        // 6. Sample data, only when the tests ask for it
        if (loadSampleData) {
            loadSampleData(io, new SampleDataLoader(studentManager, gradeManager, subjectCatalog));
        }

        // 7. Screens, in menu order
        StudentLookup lookup = new StudentLookup(io, studentManager);
        StudentDetailsView detailsView = new StudentDetailsView(io, gradeManager);
        List<Screen> screens = List.of(
                new AddStudentScreen(io, studentManager, detailsView),
                new ViewStudentsScreen(io, studentManager, gradeManager, statisticsService),
                new RecordGradeScreen(io, lookup, gradeManager, subjectCatalog),
                new GradeReportScreen(io, lookup, reportGenerator),
                new ExportReportScreen(io, lookup, gradeManager, exportService),
                new GpaScreen(io, lookup, gradeManager, gpaService),
                new BulkImportScreen(io, importService, importFolder),
                new StatisticsScreen(io, statisticsService, subjectCatalog),
                new SearchScreen(io, searchService, gradeManager, exportService, detailsView));
        return new ConsoleMenu(io, screens);
    }

    private static void prepareImportFolder(ConsoleIO io, ImportFolder importFolder) {
        try {
            importFolder.prepare();
        } catch (FileExportException e) {
            io.showError(e);   // the app still works; only the starter file is missing
        }
    }

    private static void loadSampleData(ConsoleIO io, SampleDataLoader loader) {
        try {
            loader.load();
        } catch (InvalidInputException | InvalidGradeException | SubjectNotFoundException e) {
            io.showError(e);
        }
    }
}