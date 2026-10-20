# PR #8: Bulk import grades (US-4)

**Branch:** `feature/bulk-import` → `develop`

## What does this PR do?
Imports grades from a CSV file, skipping bad rows and writing an import log.

## Changes
- `CSVParser` interface, `CsvRecord`, `SimpleCSVParser` (header check, blank lines, BOM, trimming).
- `BulkImportService` depends on the `CSVParser` and `FileExporter` **interfaces** (Dependency Inversion).
- Each row is checked for column count, student, subject/type and grade; reasons are collected.
- `ImportResult` with totals, failures and the log file path.
- Sample files `imports/sample_grades.csv` (13 good + 2 bad rows) and `imports/invalid_format.csv`.

## Merge notes
`develop` moved on while this branch was open (PR #7 was merged), so `develop` was merged
into this branch first and the tests were re-run before opening this PR.

## How was it tested?
- `SimpleCSVParserTest` with `@TempDir`: valid, BOM, blank lines, missing file, unreadable, empty, wrong header, header only.
- `BulkImportServiceTest`: **mock** parser and exporter; each failure type, log contents via `ArgumentCaptor`, `never()` checks.

## Example
```
IMPORT SUMMARY
Total Rows: 15
Successfully Imported: 13
Failed: 2

Failed Records:
  Row 7: Invalid student ID (STU999)
  Row 12: Grade out of range or not a number (105)
```
