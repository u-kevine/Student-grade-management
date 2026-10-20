# PR #7: Student search (US-6)

**Branch:** `feature/student-search` → `develop`

## What does this PR do?
Search by exact ID, partial case-insensitive name, average range and student type.

## Changes
- `SearchService` using `StudentManager.findById()` and the `Searchable` interface.

## How was it tested?
- `SearchServiceTest` with **mock** managers: partial names ("JOHN" finds "Alice Johnson"),
  range edges are included, students without grades are skipped, invalid input is rejected.
