package com.gradesystem.util;

/**
 * All fixed values used by the application live here, so there are no
 * "magic numbers" or repeated strings spread around the code.
 *
 * <p>The class is {@code final} and has a private constructor because it is
 * only a holder for constants: nobody should create an object of it.</p>
 */
public final class AppConstants {

    private AppConstants() {
        // utility class: do not instantiate
    }

    // ---------- Grades ----------
    /** Lowest grade a student can get. */
    public static final double MIN_GRADE = 0.0;
    /** Highest grade a student can get. */
    public static final double MAX_GRADE = 100.0;

    /** Minimum percentage for an "A" in the class distribution (90-100). */
    public static final double BAND_A_MIN = 90.0;
    /** Minimum percentage for a "B" in the class distribution (80-89). */
    public static final double BAND_B_MIN = 80.0;
    /** Minimum percentage for a "C" in the class distribution (70-79). */
    public static final double BAND_C_MIN = 70.0;
    /** Minimum percentage for a "D" in the class distribution (60-69). */
    public static final double BAND_D_MIN = 60.0;

    // ---------- Students ----------
    /** Passing grade for a regular student. */
    public static final double REGULAR_PASSING_GRADE = 50.0;
    /** Passing grade for an honors student. */
    public static final double HONORS_PASSING_GRADE = 60.0;
    /** Average an honors student needs to stay honors eligible. */
    public static final double HONORS_ELIGIBILITY_AVERAGE = 85.0;
    /** Youngest allowed student age. */
    public static final int MIN_AGE = 5;
    /** Oldest allowed student age. */
    public static final int MAX_AGE = 100;
    /** Longest allowed student name. */
    public static final int MAX_NAME_LENGTH = 50;
    /** Status every new student starts with. */
    public static final String STATUS_ACTIVE = "Active";

    /** Student type label for regular students. */
    public static final String TYPE_REGULAR = "Regular";
    /** Student type label for honors students. */
    public static final String TYPE_HONORS = "Honors";

    // ---------- Subjects ----------
    /** Subject type label for core subjects. */
    public static final String TYPE_CORE = "Core";
    /** Subject type label for elective subjects. */
    public static final String TYPE_ELECTIVE = "Elective";

    // ---------- IDs and dates ----------
    /** Format for student IDs: STU001, STU002 ... */
    public static final String STUDENT_ID_FORMAT = "STU%03d";
    /** Format for grade IDs: GRD001, GRD002 ... */
    public static final String GRADE_ID_FORMAT = "GRD%03d";
    /** Date format shown to the user. */
    public static final String DATE_PATTERN = "dd-MM-yyyy";
    /** Timestamp used in log lines. */
    public static final String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** Timestamp used in import log file names. */
    public static final String FILE_TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";

    // ---------- Files and folders ----------
    /** Folder where exported reports are saved. */
    public static final String REPORTS_DIR = "reports";
    /** Folder where CSV files are read from. */
    public static final String IMPORTS_DIR = "imports";
    /** Folder for the application log and import logs. */
    public static final String LOGS_DIR = "logs";
    /** Name of the application log file. */
    public static final String APP_LOG_FILE = "app.log";
    /** Extension of exported reports. */
    public static final String TEXT_EXTENSION = ".txt";
    /** Extension of import files. */
    public static final String CSV_EXTENSION = ".csv";
    /** Name (without .csv) of the ready-to-use import file created in the imports folder. */
    public static final String STARTER_IMPORT_FILE = "grades";
    /** Prefix of import log files. */
    public static final String IMPORT_LOG_PREFIX = "import_log_";
    /** Bytes in one kilobyte (for showing file sizes). */
    public static final double BYTES_PER_KB = 1024.0;

    // ---------- CSV ----------
    /** The header line every import file must start with. */
    public static final String CSV_HEADER = "StudentID,SubjectName,SubjectType,Grade";
    /** Separator between CSV columns. */
    public static final String CSV_SEPARATOR = ",";
    /** Number of columns every CSV row must have. */
    public static final int CSV_COLUMN_COUNT = 4;

    // ---------- GPA analysis ----------
    /** GPA at or above this is "excellent". */
    public static final double GPA_EXCELLENT = 3.5;
    /** GPA at or above this is "good". */
    public static final double GPA_GOOD = 3.0;
    /** GPA at or above this is "satisfactory". */
    public static final double GPA_SATISFACTORY = 2.0;

    // ---------- Console layout ----------
    /** Length of the bars in the grade distribution chart. */
    public static final int BAR_LENGTH = 20;
    /** Thin separator line used under titles. */
    public static final String LINE = "─────────────────────────────────────────────";
    /** Wide separator line used around tables. */
    public static final String WIDE_LINE =
            "─────────────────────────────────────────────────────────────────────";
}
