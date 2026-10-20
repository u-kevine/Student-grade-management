package com.gradesystem.service;

import com.gradesystem.util.AppConstants;

import java.nio.file.Path;

/**
 * What the user needs to know after an export: where the file is and how big it is.
 */
public class ExportResult {

    private final Path filePath;
    private final long sizeInBytes;

    /**
     * Creates a new ExportResult.
     *
     * @param filePath    the written file
     * @param sizeInBytes its size
     */
    public ExportResult(Path filePath, long sizeInBytes) {
        this.filePath = filePath;
        this.sizeInBytes = sizeInBytes;
    }

    /** {@return the full path of the file} */
    public Path getFilePath() { return filePath; }

    /** {@return just the file name, for example "alice.txt"} */
    public String getFileName() { return filePath.getFileName().toString(); }

    /** {@return the folder, for example "./reports/"} */
    public String getLocation() {
        Path parent = filePath.getParent();
        if (parent == null) {
            return "./";
        }
        return "./" + parent + "/";
    }

    /** {@return the size in bytes} */
    public long getSizeInBytes() { return sizeInBytes; }

    /** {@return the size as text, for example "2.4 KB"} */
    public String getFormattedSize() {
        return String.format("%.1f KB", sizeInBytes / AppConstants.BYTES_PER_KB);
    }
}
