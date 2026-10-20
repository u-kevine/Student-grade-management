package com.gradesystem.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Sets up application-wide logging (Java's built-in {@code java.util.logging}).
 *
 * <p>Every log line goes to {@code logs/app.log} with a timestamp, for example:</p>
 * <pre>[2026-10-18 14:05:11] WARNING ConsoleIO - StudentNotFoundException: Student with ID 'STU999' not found</pre>
 *
 * <p>Usage in any class:</p>
 * <pre>private static final Logger LOGGER = AppLogger.getLogger(MyClass.class);</pre>
 *
 * <p>Logs are NOT printed to the console, so they never mess up the menus.</p>
 */
public final class AppLogger {

    /** All our classes live under this package, so configuring it configures them all. */
    private static final String ROOT_PACKAGE = "com.gradesystem";

    /** Kept in a field so Java does not garbage-collect our configured logger. */
    private static final Logger ROOT_LOGGER = Logger.getLogger(ROOT_PACKAGE);

    private static boolean configured = false;

    private AppLogger() {
        // utility class: do not instantiate
    }

    /**
     * Returns a logger for the given class, setting up the log file the first time.
     *
     * @param type the class that wants to log
     * @return a ready-to-use logger
     */
    public static Logger getLogger(Class<?> type) {
        configure();
        return Logger.getLogger(type.getName());
    }

    private static synchronized void configure() {
        if (configured) {
            return;
        }
        configured = true;
        ROOT_LOGGER.setUseParentHandlers(false);   // do not print logs on the console
        ROOT_LOGGER.setLevel(Level.INFO);
        try {
            Path logFolder = Paths.get(AppConstants.LOGS_DIR);
            Files.createDirectories(logFolder);
            String logFile = logFolder.resolve(AppConstants.APP_LOG_FILE).toString();
            FileHandler fileHandler = new FileHandler(logFile, true);   // true = append
            fileHandler.setFormatter(new TimestampFormatter());
            ROOT_LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            // Logging must never crash the app, so we just warn and carry on.
            System.err.println("Warning: could not open the log file (" + e.getMessage() + ")");
        }
    }

    /**
     * Writes each log entry as one readable line that starts with a timestamp.
     */
    static class TimestampFormatter extends Formatter {

        private static final DateTimeFormatter TIMESTAMP =
                DateTimeFormatter.ofPattern(AppConstants.LOG_TIMESTAMP_PATTERN);

        @Override
        public String format(LogRecord record) {
            String className = record.getLoggerName();
            String shortName = className.substring(className.lastIndexOf('.') + 1);
            String line = String.format("[%s] %-7s %s - %s%n",
                    LocalDateTime.now().format(TIMESTAMP),
                    record.getLevel().getName(),
                    shortName,
                    formatMessage(record));
            if (record.getThrown() != null) {
                line = line + "    caused by: " + record.getThrown() + System.lineSeparator();
            }
            return line;
        }
    }
}
