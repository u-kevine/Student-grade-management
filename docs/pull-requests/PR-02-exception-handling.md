# PR #2: Custom exceptions and input validation (US-1)

**Branch:** `feature/exception-handling` → `develop`

## What does this PR do?
Adds a custom exception for every error scenario and a validator that checks input
before it is used.

## Changes
- `GradeSystemException` base class with a **recovery hint** for the user.
- `StudentNotFoundException`, `InvalidGradeException`, `InvalidFileFormatException`,
  `SubjectNotFoundException`, `InvalidInputException`, `NoGradesException`,
  `FileImportException`, `FileExportException`, `InputClosedException`.
- `InputValidator`: names, ages, emails, phones, grades, file names, search ranges.

## How was it tested?
- `ExceptionMessagesTest`: every message and hint (100% of exception code covered).
- `InputValidatorTest`: valid and invalid values for every rule.
- `FormattingAndMathTest`, `AppLoggerTest`: utilities from PR #1.

## Example
```
✗ ERROR: StudentNotFoundException
  Student with ID 'STU999' not found in the system.

  Available student IDs: STU001, STU002, STU003, STU004, STU005
```
