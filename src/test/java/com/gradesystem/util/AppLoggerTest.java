package com.gradesystem.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppLoggerTest {

    @Test
    void createsLoggerAndLogFile() {
        Logger logger = AppLogger.getLogger(AppLoggerTest.class);
        logger.info("test message");
        assertEquals(AppLoggerTest.class.getName(), logger.getName());
        assertTrue(Files.exists(Paths.get(AppConstants.LOGS_DIR, AppConstants.APP_LOG_FILE)));
    }

    @Test
    void formatterAddsTimestampLevelAndClassName() {
        LogRecord record = new LogRecord(Level.WARNING, "Something happened");
        record.setLoggerName("com.gradesystem.service.StudentManager");
        String line = new AppLogger.TimestampFormatter().format(record);
        assertTrue(line.matches("\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}] WARNING StudentManager - Something happened\\R"),
                line);
    }

    @Test
    void formatterIncludesTheCause() {
        LogRecord record = new LogRecord(Level.SEVERE, "Write failed");
        record.setLoggerName("Test");
        record.setThrown(new java.io.IOException("disk full"));
        String text = new AppLogger.TimestampFormatter().format(record);
        assertTrue(text.contains("caused by: java.io.IOException: disk full"));
    }
}
