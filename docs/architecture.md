# Architecture

## Modules

| Module | Holds |
| --- | --- |
| `:app` | The activity, the navigation host, and the DI root. |
| `:ui` | The design system: components, icons, theme, dimensions, and modifiers. |
| `:core` | Shared UI and utilities that do know about domain models: charts, formatting, logging, text markup, search, and validation. |
| `:domain` | Models, use case and contract interfaces, unit conversion, and validation. |
| `:functionality:*` | Implementations of the domain interfaces: `database` (Room), `preference` (DataStore), `backup` (the export format and its schedule), and `musclebitmap`. |
| `:feature:*` | One screen or group of screens each: a `ViewModel`, a `ScreenState`, an `Action`, and the composables. |
| `:navigation` | Route data and the `NavigationCommander` features use to navigate. |

### `:ui` versus `:core`

This follows the split [Now in Android][nia] draws between `core:designsystem` and `core:ui`:
the design system is not allowed to depend on the data layer, because it must not render models.

`:ui` therefore does not have `:domain` on its compile classpath, and the `liftapp.android.compose`
convention plugin deliberately does not put it there — modules that render domain models ask for
`:domain` themselves. This is what keeps the boundary honest: a domain import in `:ui` does not
compile.

`:core` sits on the other side of that line. It depends on both `:ui` and `:domain`, and is where a
component belongs as soon as it needs to know what a `Name`, a `Goal`, or an `ExerciseSet` is.

### `:domain`

`:domain` is a plain `java-library`, not an Android module, and nothing in it imports `android.*` or
`androidx.*`. Keeping it that way is what lets its tests run on the JVM without Robolectric.

## Use cases and contracts

Both are usually a `fun interface` that a repository implements directly, so there is no separate
class and no extra hop. The two names are not interchangeable:

- **`*UseCase`** is what the presentation layer injects. A `ViewModel` depends on these.
- **`*Contract`** is a data-layer port consumed by a use case. A `ViewModel` should not inject one.

A use case is only a class when it does something a repository call does not:

- handles cancellation or switches dispatcher, as `InsertExercisesUseCase` does with
  `withContext(NonCancellable)`,
- binds route arguments into the call, as `SaveGoalUseCase` does with `ExerciseGoalRouteData`,
- or composes several sources, as `GetEditableWorkoutUseCase` does.

Pure delegation does not earn a class. Write it as a `fun interface` and let the repository
implement it; it stays just as easy to fake in a test:

```kotlin
val getExercise = GetExerciseUseCase { flowOf(exercise) }
```

## The backup format

A backup is a ZIP named `.lfa`, as in the published app. It holds a `manifest.json` naming the format
version and what was written, and one CSV per database table under a directory named after the
[`BackupDataType`][type] it belongs to. Preferences travel as a CSV of their own.

Nothing in the reader or the writer names a column. The header row is whatever the table declares, so
the file describes the schema it came from rather than a parallel description of it that could drift.
Two details are worth knowing before touching it:

- **A quoted field is a value; a bare field is `NULL`.** Plenty of columns are nullable and an empty
  string is not the same as no value in any of them.
- **The import runs with foreign keys off and checks them once before committing.** `INSERT OR
  REPLACE` on a referenced row would otherwise cascade the delete it does first — restoring a
  routines-only backup would take every workout started from those routines with it.

Categories are not independent: a workout references the routine it came from, so
[`BackupDataType.dependencies`][type] is what both the picker and the import expand a selection with.

[type]: ../domain/src/main/kotlin/com/patrykandpatryk/liftapp/domain/backup/BackupDataType.kt
[nia]: https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md
