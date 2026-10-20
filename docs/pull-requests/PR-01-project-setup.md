# PR #1: Project setup (Maven, constants, logging)

**Branch:** `feature/project-setup` → `develop`

## What does this PR do?
Turns the Lab 1 folder into a Maven project so we can add JUnit, Mockito and coverage,
and adds the shared building blocks every later feature needs.

## Changes
- `pom.xml` with JUnit 5, Mockito, JaCoCo (build fails below 80% line coverage), exec and jar plugins.
- `.gitignore` for `target/`, `logs/`, `reports/` and IDE files.
- `AppConstants`: every fixed value in one place (no magic numbers).
- `MathUtils`, `TextFormat`: shared average/percentage/formatting helpers (no duplication).
- `AppLogger`: timestamped logging to `logs/app.log`.

## How was it tested?
- `mvn compile` succeeds. Tests for these utilities arrive with PR #2.
