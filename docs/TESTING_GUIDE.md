# Testing Guide

This guide explains how to run the automated tests, how to read the reports, and how
to walk through the 8 manual test scenarios from the project brief.

---

## 1. Running the automated tests

### Command line

| Command | What it does |
|---|---|
| `mvn test` | Compiles everything and runs all 185 tests |
| `mvn verify` | Same as `mvn test`, then **fails** if line coverage is below 80% |
| `mvn test -Dtest=GPACalculatorTest` | Runs only one test class |
| `mvn test -Dtest=StatisticsCalculatorTest#medianWithEvenCountAveragesTheTwoMiddleValues` | Runs one single test |

A successful run ends with:

```
[INFO] Tests run: 185, Failures: 0, Errors: 0, Skipped: 0
[INFO] All coverage checks have been met.
[INFO] BUILD SUCCESS
```

### IntelliJ IDEA

- Right-click `src/test/java` → **Run 'All Tests'**.
- Right-click `src/test/java` → **More Run/Debug → Run 'All Tests' with Coverage** to see
  coverage directly in the editor (green = tested line, red = untested line).
- Click the green ▶ next to any test method to run just that test.

---

## 2. Reading the reports

| Report | Location | How to open |
|---|---|---|
| Test results (per class) | `target/surefire-reports/*.txt` | Any text editor |
| Test results (for CI tools) | `target/surefire-reports/TEST-*.xml` | Used by GitHub Actions |
| Coverage report | `target/site/jacoco/index.html` | Open in a browser, click a package, then a class |

In the coverage report, **green** lines were run by a test, **red** lines were never run,
and **yellow** lines are `if` statements where only one side was tested.

---

## 3. What is tested

| Kind of test | Test classes | What they prove |
|---|---|---|
| **Unit tests** (one class on its own) | `FourPointGradingScaleTest`, `StatisticsCalculatorTest`, `InputValidatorTest`, `ModelTest`, `ExceptionMessagesTest`, `StudentManagerTest`, `GradeManagerTest`, `SubjectCatalogTest`, `ReportGeneratorTest`, `SimpleCSVParserTest`, `TextFileExporterTest`, `ConsoleIOTest`, `FormattingAndMathTest`, `AppLoggerTest`, `SampleDataLoaderTest` | Each class does its own job correctly, including edge cases (empty lists, band edges like 92.9% vs 93%, even/odd median, missing files). |
| **Integration tests with Mockito** | `BulkImportServiceTest`, `ExportServiceTest`, `GPAServiceTest`, `SearchServiceTest`, `StatisticsServiceTest`, `GPACalculatorTest` | Services work with their dependencies. Mocks replace files and storage, so the tests are fast and never touch the disk. `verify(...)` checks what was called; `ArgumentCaptor` checks what was written. |
| **End-to-end tests** | `ApplicationFlowTest` | Starts the real application with `Main.createMenu()`, types scripted input into every menu option and checks the printed output, including every error message from the brief. |

### How a Mockito test works (example from `ExportServiceTest`)

```java
FileExporter exporter = mock(FileExporter.class);                 // 1. a fake file writer
when(exporter.writeToFile(anyString(), anyString(), anyString()))
        .thenReturn(Paths.get("reports", "alice.txt"));           // 2. decide what it returns

service.exportStudentReport("STU001", ReportType.DETAILED, "alice"); // 3. run the real code

verify(exporter).writeToFile(eq("reports"), eq("alice.txt"), anyString()); // 4. check the call
```

No file is created, but we still know the service asked for the right folder and file name.

---

## 4. Manual test scenarios (from the brief)

Start the app (`mvn compile exec:java`) and answer **Y** to load sample data
(students STU001-STU005, 5 grades each), unless the scenario says otherwise.

### Scenario 1: Exception handling
1. Choose **3**, enter `STU999` → `✗ ERROR: StudentNotFoundException` with the list of IDs. Type `Y`.
2. Enter `STU001`, choose `1` (Core), `1` (Mathematics), enter `150` → `✗ ERROR: InvalidGradeException`, "You entered: 150". Type `Y`, enter `95` → "✓ Grade recorded successfully!".
3. Choose **7**, enter `invalid_format` → `✗ ERROR: InvalidFileFormatException`.
4. Open `logs/app.log`: each error is there with a timestamp.

*Automated in:* `ApplicationFlowTest.recordGradeShowsBothExceptionsFromTheBrief`, `bulkImportErrors`.

### Scenario 2: Export grade report
1. Choose **5**, enter `STU001`, choose `2` (Detailed), file name `alice_johnson_report`.
2. The app shows "✓ Report exported successfully!", the location `./reports/` and the size.
3. Open `reports/alice_johnson_report.txt` and check the grades and averages.

*Automated in:* `ApplicationFlowTest.exportReportWritesFileInReportsFolder`, `ExportServiceTest`.

### Scenario 3: Calculate GPA
1. Choose **6**, enter `STU002` (Bob Johnson).
2. Check each row against the GPA table in the README, e.g. 92% → 3.7 (A-), 85% → 3.0 (B).
3. Cumulative GPA = average of the points = (3.7+3.0+3.3+3.7+2.7)/5 = **3.28**, letter **B**, Class Rank **2 of 5**.
4. Try `STU004` (excellent, 3.60) and `STU003` (needs improvement, 1.26) for other ranges.

*Automated in:* `FourPointGradingScaleTest` (every row and edge), `GPAServiceTest`, `ApplicationFlowTest.gpaScreenShowsBreakdownRankAndAnalysis`.

### Scenario 4: Bulk import
1. Look at `imports/sample_grades.csv`: 13 valid rows and 2 invalid ones (row 7 = `STU999`, row 12 = grade `105`).
2. Choose **7**, enter `sample_grades`.
3. The summary shows Total 15, Imported 13, Failed 2, with the two reasons.
4. Open the `logs/import_log_...txt` file named on screen.
5. Choose **4**, enter `STU001`: the imported grades appear in the history.

*Automated in:* `BulkImportServiceTest`, `SimpleCSVParserTest`, `ApplicationFlowTest.bulkImportOfSampleFileGivesThirteenOkAndTwoFailed`.

### Scenario 5: Class statistics
1. Choose **8**. With the sample data: 5 students, 25 grades.
2. Check: Mean 80.5%, Median 82.0%, Mode 85.0%, Highest 100% (STU004 - Science), Lowest 42% (STU003 - Mathematics).
3. The distribution shows 5 A, 10 B, 7 C, 2 D, 1 F.

*Automated in:* `StatisticsCalculatorTest` (including a textbook standard-deviation example), `StatisticsServiceTest`, `ApplicationFlowTest.statisticsScreenShowsEverySection`.

### Scenario 6: Search students
1. Choose **9**, `2` (name), type `John` → Alice Johnson and Bob Johnson.
2. Choose `3` (new search), `3` (range), `80` and `90` → only students whose average is in that range.
3. Choose `3`, `4` (type), `2` (Honors) → Bob Johnson and David Lee.
4. Try action `2` to export results to `reports/`.

*Automated in:* `SearchServiceTest`, `ApplicationFlowTest.searchByNameThenViewDetailsAndExport`, `searchByIdRangeAndType`.

### Scenario 7: Unit tests
Run `mvn verify` and open `target/site/jacoco/index.html`. All tests pass and coverage is above 80%.

### Scenario 8: Git workflow
See [GIT_WORKFLOW.md](GIT_WORKFLOW.md). Quick checks:

```bash
git log --oneline --graph --all   # history with feature branches merged into develop
git log --oneline | wc -l         # number of commits
git branch -a                     # main, develop and the feature/* branches
```

---

## 5. Debugging tips

- Set a **breakpoint** (click next to a line number in IntelliJ) inside `BulkImportService.importRow`
  and run `BulkImportServiceTest` in **Debug** mode to watch each CSV row being checked.
- When a test fails, read the message first: `expected: <3.5> but was: <3.28>` tells you the
  value the test wanted and the value the code produced.
- `logs/app.log` shows every action and error with a timestamp, which helps you find out
  what happened before a problem.
