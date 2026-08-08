# AGENTS.md

Guidance for agents working in the **LiftApp** codebase.

**Architecture lives in [docs/architecture.md](docs/architecture.md)** — module layout and
dependency direction, the convention plugins, the domain model, the data layer, the feature layer,
navigation, DI, coroutines, and the design system. Read it before making changes. This file covers
what surrounds that: the domain vocabulary, how to build and format, and where to start.

## What this is

An offline Android workout tracker. The domain vocabulary is specific and the models are distinct —
don't conflate them:

- **Exercise** — a movement, with a type and the muscles it targets. Users can define their own.
- **Routine** — an ordered collection of exercises, supersets included, each carrying a goal (sets,
  rep range, rest time).
- **Plan** — a repeating schedule of routines and rest days. One plan is active at a time.
- **Workout** — a logged session against a routine: sets, reps, weight, distance, duration.
- **Body measurement** — weight, body fat, circumferences, tracked over time with charts.

Plus a one-rep-max calculator and backup/restore to a `.lfa` file, on demand or on a schedule.

**There is no network layer, backend, or account system, and nothing should add one.** Room holds
the domain data; DataStore holds user settings.

The app is a rewrite of a legacy *LiftApp*, still published as `pl.patrykgoworowski.mintlift` — which is why
`:app`'s `applicationId` does not match its namespace, and why the Room schema is past version 12
with the published app's migrations behind it.

## Stack

Compose and Material 3 behind a custom design system, Hilt via KSP, Room, DataStore, Navigation
Compose with type-safe serializable routes, WorkManager for scheduled backups, Paging 3 for the
journal, Vico for charts, Glide and Landscapist for images, Timber for logging, and
kotlinx.serialization for both persistence and routes. Kotlin, JVM toolchain 21.

**Do not state a version from memory — read `gradle/libs.versions.toml`.** It is the single source
for every dependency and plugin version; the SDK levels are in
`build-logic/convention/src/main/kotlin/Conventions.kt`.

## Build, run & format

```bash
./gradlew assembleDebug     # debug APK → app/build/outputs/apk/debug/
./gradlew testDebug         # all unit tests
./gradlew :feature:workout:testDebug   # one module
```

Debug builds install alongside the released app — their application ID carries a `.dev` suffix.

Formatting is **ktfmt in kotlinlang style** (4-space indent, matching `kotlin.code.style=official`)
and linting is **Detekt** with a ktlint-wrapper ruleset, config in `detekt.yml`. Both run as Gradle
tasks — don't download a ktfmt jar, and don't hand-format:

```bash
./gradlew ktfmtFormat       # format in place
./gradlew ktfmtCheck        # check only; CI fails on any deviation
./gradlew detektCheck       # lint
```

**`build-logic` is a separate included build and those tasks do not reach it.** CI checks it
separately, so run it too when you touch a convention plugin:

```bash
./gradlew -p build-logic :convention:ktfmtFormat
./gradlew -p build-logic :convention:detektMain :convention:detektTest
```

There is no `.editorconfig`; ktfmt's kotlinlang style is the single source of truth.

Five workflows in `.github/workflows/`: `build-debug-apk`, `run-tests`, `check-formatting`, and
`lint` on every push, plus `build-release` on a `v*.*.*` tag.

## Adding a module

Apply a convention plugin rather than hand-writing Gradle config — see
[the convention plugins](docs/architecture.md#the-convention-plugins). A feature module's build file
is two lines:

```kotlin
plugins { id("liftapp.android.feature") }

android { namespace = "com.patrykandpatrick.liftapp.feature.onerepmax" }
```

`liftapp.android.feature` already brings `:domain`, `:core`, and `:navigation`, so declare only what
is extra, through the `libs.*` accessors and the `kotlin`, `hilt`, `ui`, and `testing` bundles.

Register the module in `settings.gradle.kts`.

## Testing

JVM unit tests under `src/test/`; there are no instrumented tests. `:domain-testing` holds shared
fixtures, consumed as `testImplementation`. Coverage is uneven — heaviest in
`:functionality:database`, `:functionality:backup`, `:domain`, and `:feature:home`, thin elsewhere.
When adding logic to `:domain` or `:functionality:*`, add unit tests alongside it; use Turbine for
`Flow`s.

Two things that will bite otherwise:

- `:functionality:*` modules run on the **JUnit Platform**, and need the Jupiter engine and the
  platform launcher on the test runtime classpath. Without them Gradle 9 fails outright and Gradle 8
  silently discovered nothing. `:functionality:database` also adds the Vintage engine, because the
  Room migration tests are JUnit 4.
- The **database migration tests run the real schema under Robolectric**, and `MigrationTestHelper`
  reads the exported schema JSON as test assets — including the published app's `11.json`.

## Quick pointers

- **Add a screen/feature** → new `feature/<name>` module (`feature/onerepmax` is the simplest
  template), then register it in `app/.../ui/Root.kt` and its route in `navigation/`. See
  [the feature layer](docs/architecture.md#the-feature-layer).
- **Change persistence** → `functionality/database`. Changing the schema means a version bump, a
  migration, a new exported JSON, and a migration test — see
  [the data layer](docs/architecture.md#the-data-layer).
- **Add a user setting** → `functionality/preference` plus the `PreferenceRepository` interface in
  `domain/preference`.
- **Navigate from a ViewModel** → inject `NavigationCommander`; don't touch `NavController`.
  See [navigation](docs/architecture.md#navigation).
- **Add a shared UI component** → `ui/` for a design-system primitive, `core/ui/` if it composes
  several or needs a domain model. The build enforces this: `:ui` cannot see `:domain`, and adding
  it there is the wrong fix. See [`:ui` versus `:core`](docs/architecture.md#ui-versus-core).
- **Touch the backup format** → read [the backup format](docs/architecture.md#the-backup-format)
  first; the CSV null convention and the foreign-key handling are both easy to break.
