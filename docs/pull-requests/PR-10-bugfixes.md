# PR #10: Bug fixes found during manual testing

## `bugfix/search-export-newline` → `develop`
**Bug:** after exporting search results, the success message was printed on the same line
as the file name prompt: `Enter filename (without extension): ✓ Search results exported ...`
**Fix:** print an empty line before the success message (`SearchScreen.exportResults`).

## `bugfix/statistics-alignment` → `develop`
**Bug:** in Class Statistics, "Physical Education:" is longer than the column width, so its
average was pushed out of line with the other subjects. The menu `[NEW]` tags were also
squeezed against the longest labels.
**Fix:** widened the subject column (`StatisticsScreen`) and the menu label column (`ConsoleMenu`).

## How was it tested?
Manual run of options 8 and 9; `mvn verify` still passes.
