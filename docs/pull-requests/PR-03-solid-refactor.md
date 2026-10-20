# PR #3: Refactor models and storage (SOLID)

**Branch:** `feature/solid-refactor` → `develop`

## What does this PR do?
Rebuilds the Lab 1 classes in packages and fixes the design problems that made them hard to test.

## Changes
- `Student` no longer holds a `GradeManager` (Single Responsibility).
- `isHonorsEligible()` on every student replaces the `instanceof HonorsStudent` check (Liskov Substitution).
- `Searchable` and `Exportable` interfaces (Interface Segregation).
- `Grade` validates in its constructor, so an invalid grade can never exist.
- `StudentManager` / `GradeManager` use `ArrayList` (no fixed limit) and generate IDs
  (no static counters, so tests do not affect each other).
- `SubjectCatalog` holds the subjects, so new subjects/types need no menu changes (Open/Closed).
- Model classes no longer print; printing moves to the UI layer.

## How was it tested?
- `ModelTest`, `StudentManagerTest`, `GradeManagerTest`, `SubjectCatalogTest`.
