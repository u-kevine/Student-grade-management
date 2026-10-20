# PR #5: Class statistics (US-5)

**Branch:** `feature/class-statistics` → `develop`

## What does this PR do?
Adds the math and data gathering for the class statistics screen.

## Changes
- `StatisticsCalculator`: mean, median (even and odd counts), mode (with ties and "no mode"),
  population standard deviation, min, max, range, grade bands.
- `StatisticsService`: distribution, highest/lowest grade with student and subject,
  subject and subject-type averages, Regular vs Honors averages.

## How was it tested?
- `StatisticsCalculatorTest`: includes the textbook set {2,4,4,4,5,5,7,9} whose standard deviation is exactly 2, and empty lists.
- `StatisticsServiceTest`: real data plus a **mock**-based empty-class test.
