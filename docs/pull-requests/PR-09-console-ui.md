# PR #9: Console UI with the new menu

**Branch:** `feature/console-ui` → `develop`

## What does this PR do?
Connects all services to a 10-option menu and removes the old Lab 1 sources.

## Changes
- `ConsoleIO`: all input/output, with "ask until valid" helpers and `showError()` (prints and logs).
- `Screen` interface and one class per menu option; `ConsoleMenu` never changes when an option is added (Open/Closed).
- `StudentLookup` (ID prompt with "Try again?") and `StudentDetailsView`, shared by several screens.
- `SampleDataLoader` and the start-up question to load it.
- `Main.createMenu()` creates and connects every object (dependency injection).
- Old `src/*.java` files deleted (replaced by the new packages).

## How was it tested?
- `ConsoleIOTest`: invalid then valid input, Y/N, end of input.
- `ApplicationFlowTest`: starts the real app and drives every menu option and error path.
