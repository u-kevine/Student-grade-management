# PR #4: GPA calculation (US-3)

**Branch:** `feature/gpa-calculation` → `develop`

## What does this PR do?
Converts percentages to the 4.0 scale, calculates cumulative GPA and class rank.

## Changes
- `GradingScale` interface + `FourPointGradingScale` (strategy pattern, Open/Closed).
- `Calculable` interface + `GPACalculator`, which receives the scale through its constructor (Dependency Inversion).
- `GPAService`: GPA per student, class rank (ties share a rank), class average GPA.

## How was it tested?
- `FourPointGradingScaleTest`: all 11 rows and the exact band edges (92.9 → A-, 93 → A).
- `GPACalculatorTest`: uses a **mock** `GradingScale` to prove the injected scale is used.
- `GPAServiceTest`: **mock** `StudentManager` and `GradeManager`; rank, ties, no grades, unknown student.
