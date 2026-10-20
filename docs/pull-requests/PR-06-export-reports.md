# PR #6: Export grade reports (US-2)

**Branch:** `feature/export-reports` → `develop`

## What does this PR do?
Builds report text and saves it to `reports/`.

## Changes
- `FileExporter` interface + `TextFileExporter` (only writes files, creates the folder).
- `ReportType` (Summary, Detailed, Both) and `ReportGenerator` (only builds text).
- `ExportService` joins them and returns the file location and size (`ExportResult`).

## How was it tested?
- `TextFileExporterTest` with JUnit `@TempDir`, including a write failure → `FileExportException`.
- `ReportGeneratorTest`: summary vs detailed contents, failing/honors lines, no grades.
- `ExportServiceTest`: **mock** `FileExporter` + `ArgumentCaptor` to check folder, file name and content without touching the disk.
