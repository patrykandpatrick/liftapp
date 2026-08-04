# LiftApp

An Android workout tracker: build routines, schedule them into a plan, and log the work you do.

Available on the [Play Store](https://play.google.com/store/apps/details?id=pl.patrykgoworowski.mintlift).

## Status

The app is on the Play Store and usable day to day, but it is still under active development —
features are being added and reworked, and interfaces are not settled. Data from the previously
published version is migrated on first launch.

## What it does

- **Exercises.** A library covering weight, calisthenics, cardio, and time-based exercises, each
  with the muscles it works, plus muscle diagrams and a per-exercise history.
- **Routines.** Ordered lists of exercises, including supersets, with a goal per exercise: sets,
  a rep range, and rest time.
- **Plans.** A repeating schedule of routines and rest days; one plan is active at a time and
  drives what the dashboard suggests next.
- **Workouts.** Log sets against the routine's goals, with previous performance in view.
- **Body measurements.** Weight and other measurements over time, with charts.
- **One-rep-max calculator.**
- **Backup and restore.** Export to a `.lfa` file, restore from one, and back up automatically on
  a schedule.
- **Preferences.** Mass and distance units, 12/24-hour time, first day of the week, and a light,
  dark, or system theme.

## Tech stack

Kotlin and Jetpack Compose with Material 3, Hilt for DI, Room for storage, DataStore for
preferences, WorkManager for scheduled work, Navigation Compose for routing, and
[Vico](https://github.com/patrykandpatrick/vico) for charts.

The build is a composite one: `build-logic` holds the convention plugins every module applies, and
`gradle/libs.versions.toml` is the single source of versions.

## Modules

The code is split into `:app`, `:ui` (the design system), `:core`, `:domain`, `:functionality:*`
(implementations of the domain's interfaces), `:feature:*` (one screen or group of screens each),
and `:navigation`. See [docs/architecture.md](docs/architecture.md) for what belongs where, why
`:ui` and `:core` are separate, and how the backup format works.

## Building

JDK 21 is required. `minSdk` is 26; the app compiles against and targets SDK 37.

```bash
./gradlew assembleDebug
```

Debug builds install alongside the released app — their application ID carries a `.dev` suffix.

Tests:

```bash
./gradlew testDebug
```

Formatting is [ktfmt](https://github.com/facebook/ktfmt) in Kotlin language style, checked in CI:

```bash
./gradlew ktfmtFormat
./gradlew -p build-logic :convention:ktfmtFormat
```

Linting uses the [Detekt Gradle plugin](https://detekt.dev/) with its ktlint ruleset and does not
auto-correct or format code:

```bash
./gradlew detektCheck
./gradlew -p build-logic :convention:detektMain :convention:detektTest
```

Install the formatting and linting pre-commit hooks with:

```bash
lefthook install
```

## Releases

Pushing a `v*.*.*` tag builds a signed APK and App Bundle and opens a draft GitHub release with the
APK attached. Version codes are derived from the assets of previous releases, so the tag is the only
thing that needs picking.

## Documentation

- [Architecture](docs/architecture.md) — modules, use cases and contracts, the backup format.
- [Workout programming](docs/workout-programming.md) — the reference the app's prescriptions are
  built against: loads, rep ranges, proximity to failure, and volume.
