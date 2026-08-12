# Architecture

## Modules

| Module | Holds |
| --- | --- |
| `:app` | The activity, the navigation host, and the DI root. |
| `:ui` | The design system: components, icons, theme, dimensions, and modifiers. |
| `:core` | Shared UI and utilities that do know about domain models: charts, formatting, logging, text markup, search, and validation. |
| `:domain` | Models, use case and contract interfaces, unit conversion, and validation. |
| `:domain-testing` | Test fixtures shared across modules, consumed as `testImplementation`. |
| `:functionality:*` | Implementations of the domain interfaces: `database` (Room), `preference` (DataStore), `backup` (the export format and its schedule), and `musclebitmap`. |
| `:feature:*` | One screen or group of screens each: a `ViewModel`, a `ScreenState`, an `Action`, and the composables. |
| `:navigation` | Route data and the `NavigationCommander` features use to navigate. |

`settings.gradle.kts` is the list that counts — `buildSrc/`, `feature/about/`, and
`functionality/storage/` are leftovers of an older layout holding untracked build output.

### Dependency direction

```
:app ──▶ :feature:* ──▶ :core ──▶ :ui
                   └──▶ :navigation
:feature:*, :core, :navigation, :functionality:* ──▶ :domain
:app ──▶ :functionality:*   (the only module that sees them)
```

**No feature sees a `:functionality:*` module.** A feature reaches the data layer through a
`:domain` interface and never names its implementation, which is what lets an implementation change
without touching a screen. `:app` depends on them without listing them — it iterates
`project(":functionality").subprojects` — so a new one joins the DI graph as soon as
`settings.gradle.kts` includes it. `:functionality:backup` is the one intra-group dependency, on
`database` and `preference`, because a backup reads across both. `:core` exposes `:ui` with `api`,
so a feature gets the design system transitively.

### `:ui` versus `:core`

The design system is not allowed to depend on the data layer, because it must not render models —
the split [Now in Android][nia] draws between `core:designsystem` and `core:ui`.

`:ui` therefore does not have `:domain` on its compile classpath, and the `liftapp.android.compose`
convention plugin deliberately does not put it there — modules that render domain models ask for
`:domain` themselves. **Do not add `:domain` to `:ui` to make an import resolve.** A component that
needs to know what a `Name`, a `Goal`, or an `ExerciseSet` is belongs in `:core`, which depends on
both.

### `:domain`

A plain `java-library`, not an Android module, and nothing in it imports `android.*` or `androidx.*`
— which is what lets its tests run on the JVM without Robolectric. Android concerns it cannot name
sit behind interfaces it declares and an upper layer implements: `StringProvider` for resources,
`Formatter` for locale-aware formatting.

## The convention plugins

The layering above is not a convention that has to be remembered; it is what the build applies.
`build-logic` is an included build holding one precompiled script plugin per module kind, so a
module's build file is a plugin id and a namespace. Apply the one that matches rather than
hand-writing Gradle config, and open it rather than guessing what it brings:
[`liftapp.jvm.library`][jvm] (`:domain` and `:domain-testing`), [`liftapp.android.library`][lib],
[`liftapp.android.compose`][compose], [`liftapp.android.feature`][feature],
[`liftapp.android.functionality`][func], [`liftapp.android.application`][app].

Two carry a constraint worth knowing before reaching for them: `compose` is layered on top of
`library` or `application` and applies no Android plugin of its own, and `functionality` runs its
tests on the JUnit Platform and brings no Compose.

[`Conventions.kt`][conventions] holds what they share: the SDK levels, `jvmToolchain(21)`, the
project-wide opt-ins, and the catalog lookups. AGP 9 brings its own Kotlin support, so
`org.jetbrains.kotlin.android` is neither applied nor needed. Dependencies come from the version
catalog through the `libs.*` accessors and the `kotlin`, `hilt`, `ui`, and `testing` bundles, not
hard-coded coordinates.

**Minification is configured by `:app` only.** Enabling it per module made every library run R8
independently; several emitted a class named `a.a`, and the app's own R8 pass then failed on the
duplicate. A library that needs to contribute keep rules uses `consumerProguardFiles`.

## Packages and naming

A module's namespace is declared explicitly and matches its path:
`com.patrykandpatrick.liftapp.<group>.<module>`. `:app` alone differs — namespace
`com.patrykandpatrick.liftapp`, `applicationId` `pl.patrykgoworowski.mintlift` (`.dev` in debug),
the identity it already has on the Play Store.

Three deviations survive, all deliberately left alone: `:feature:exercisedetails` uses
`…liftapp.feature.exercise`; `:feature:exercisegoal`'s sources drop the `liftapp` segment
(`com.patrykandpatrick.feature.exercisegoal`) though its namespace keeps it; and
`:feature:newexercise` holds its Kotlin under `src/main/java/`.

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

## The domain model

Models are plain `data class`es in feature-sliced packages — `exercise`, `routine`, `plan`,
`workout`, `bodymeasurement`, `goal`. Writes take a nested type rather than the model itself:
`Exercise.Insert`, `Exercise.Update`. Values with more than one shape are `@Serializable sealed`
hierarchies, as `Name` is over `Name.Raw` and `Name.Resource`.

**There are no value or inline classes.** A quantity is a raw `Double` or `Int` carried next to its
unit, and a unit is a `@Serializable` enum or object implementing [`ValueUnit`][unit] — `MassUnit`,
`LongDistanceUnit`, `RepsUnit`.

Validation is a `Validator` producing a `ValidationResult` over a `Validatable` that is `Valid` or
`Invalid`; `TextValidator` is the variant the form system builds on. Errors are two sealed
hierarchies: `LiftAppException` for what the domain can go wrong with, `DisplayableException` for
what is meant to reach the user.

## The data layer

### Room

[`Database`][database] is at **version 14**, with `exportSchema = true` and a handful of
`@DatabaseView`s alongside the entities. Versions 2–11 belong to the published app, so [`migration`][migration]
carries its chain (`PublishedMigrations`) plus the two paths into the rewrite: `Migration11To12`
from the Play Store build, `Migration1To12` from an early install. Everything from 12 up is the
rewrite's own — `Migration12To13` adds reps in reserve, `Migration13To14` classifies exercises and
splits the deltoid. Exported JSON sits in `functionality/database/schemas/`, also registered as a
test asset directory so `MigrationTestHelper` can read it; a fresh install is seeded by
[`DatabaseCallback`][callback].

**Changing the schema means a new version, a migration, a new exported JSON, and a migration test.**

- **Entities** are `XEntity` with snake_case tables and columns and
  `@PrimaryKey(autoGenerate = true) val id: Long = 0`. They store rich domain types directly.
- **DAOs** are `XDao`. **Reads return `Flow<…>`** and are not `suspend`; **writes are `suspend`**.
  Queries built at runtime use `@RawQuery`.
- **Converters** live in `converter/`. `JsonConverters` is `@ProvidedTypeConverter` with an injected
  `Json` and serializes the rich types — `Name`, `Goal`, `List<Muscle>`, units — to strings;
  `LocalDateTimeConverters` handles dates; enums use Room's built-in converter. `List<Muscle>` goes
  through `MuscleListSerializer`, which resolves retired constant names and drops unknown ones,
  because a `.lfa` archive replays raw column text that no migration can ever reach.
- **Mappers** split by direction: `toDomain(...)` is a method on an `@Inject`-constructed `XMapper`,
  `toEntity()` an extension function. A read is typically
  `dao.getX().map(mapper::toDomain).flowOn(dispatcher)`.
- **Repositories** are `Room<Feature>Repository` or `<Feature>RepositoryImpl`. `workout` and `plan`
  have no repository interface at all — they are reached only through the granular contracts above.

### Preferences

`:functionality:preference` implements `PreferenceRepository` over `DataStore<Preferences>`. The
`Preference<T>` abstraction is [declared in `:domain`][preference] and implemented in the module's
own `datastore/` package — it is in-repo, not a library. Enums are stored by name and richer values,
such as `ActivePlan`, as kotlinx JSON; `legacy/` migrates the published app's `SharedPreferences` on
first launch.

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

## The feature layer

A feature module is MVVM over Compose, and they all have the same shape:

```
ui/       XScreen.kt + XViewModel.kt, plus the screen's own composables
model/    Action.kt (a sealed interface), the state type, feature-local use cases
di/       Module.kt — a Hilt @Module
mapper/   mappers, where the feature needs them
```

**ViewModels** are `@HiltViewModel` extending androidx `ViewModel` directly — no shared base class.
Some mix in `SavedStateHandleViewModel` for its `saveable {}` helper, or `LogPublisher by logger`.

**Data flows one way.** The public `XScreen(viewModel = hiltViewModel())` collects with
`collectAsStateWithLifecycle()` and delegates to a `private` stateless
`XScreen(state, onAction, modifier)`. Events go back up through `onAction: (Action) -> Unit`, which
the ViewModel handles in a `when` over the sealed `Action`.

**State reaches the screen three ways**, all current: a `StateFlow<Loadable<T>>` from
`Flow.toLoadableStateFlow(scope)` (the common case); Compose `State` via `saveable(...)`, where it
must survive process death; or a `@Stable` holder built by an `@AssistedFactory`, where the screen
drives something stateful of its own. Loading and failure are not per-screen enums:
[`Loadable<T>`][loadable] is a sealed `Success`/`Error`/`Loading` in `:domain`, and `:core` adds the
`@Composable Loadable<T>.Unfold(onLoading, onError, onSuccess)` renderer that unpacks it. Stability
rides on `@Immutable`/`@Stable` on the state types, even when they hold a plain `List`;
`kotlinx.collections.immutable` is available and a few features use it, but annotating the holder is
the default.

**Forms** go through [`TextFieldState<T>`][textfield] in `:core`, one subclass per type, produced by
an injected `TextFieldStateManager`. Each wraps a Compose `TextFieldValue` and a `TextValidator`, is
passed inside `Action`s, and is validated before anything is persisted. Previews use
[`@MultiDevicePreview`][previews], which fans out light and dark across device sizes.

Two modules hold several screens because the screens belong together: `:feature:home` has the five
bottom-bar tabs (`dashboard`, `plan`, `exercises`, `bodymeasurementlist`, `more`) plus `journal`, and
`:feature:plan` has `creator`, `list`, and `configurator` — each under its own subpackage.

## Navigation

Type-safe and serialization-based. There is no navigation XML anywhere in the project.

- **Routes are declared centrally** in [`Routes.kt`][routes] — one `object Routes` of nested
  `@Serializable` destinations — with a `*RouteData` class per parameterized route in
  `navigation/data/`.
- **The NavHost lives in [`Root.kt`][root]**, not in the features. A feature exports a public
  `XScreen()` and nothing else; `Root.kt` registers it with a `NavGraphBuilder.addX()` extension
  calling `composable<XRouteData> { XScreen() }`. Features do not self-register graphs.
- **ViewModels do not touch `NavController`.** [`NavigationCommander`][commander] is a `@Singleton`
  in `:domain` emitting `NavigationCommand`s over a `SharedFlow`; `Root.kt` collects them and drives
  the real controller. Results reach a previous screen via `publishResult`/`getResults<T>`.
- **Route arguments arrive by injection**: each feature's Hilt module provides
  `fun provideXRouteData(handle: SavedStateHandle): XRouteData = handle.toRoute()`.
- **Deep links** use the `liftapp` scheme, declared in [`DeepLink`][deeplink]:
  `workout/{routineID}/{workoutID}` and
  `body-measurement/{bodyMeasurementID}/entry/{bodyMeasurementEntryID}`. The activity also accepts
  `application/octet-stream` and `text/csv`, so a file manager can open a backup with the app.
- **The bottom bar has its own [`Navigator`][bottombar]** — a custom `androidx.navigation.Navigator`
  that keeps the bar stable and each tab's state saved across the five tabs.

## Dependency injection

Hilt modules are written as **an `interface` of `@Binds` methods plus a `companion object` of
`@Provides`**. `@Binds` covers interface-to-implementation — repositories, use case contracts,
mappers; `@Provides` covers what has to be constructed: the Room database and its DAOs, DataStore,
`Json`, formatters.

**Where a binding installs is a deliberate choice.** Cross-cutting infrastructure goes into
`SingletonComponent` (`DatabaseModule`, `PreferenceModule`, `CoreModule`, `DomainModule`,
`BackupModule`); per-feature and per-use-case bindings go into `ViewModelComponent` and die with the
screen.

Multibindings are the extension point twice over: `@IntoMap` in [`SummaryModule`][summary] keys the
exercise-set summary mappers by `ExerciseSummaryType`, and `@IntoSet` in `DatabaseModule`
contributes polymorphic serializers to the `Json` that `DomainModule.provideJson` builds. The entry
points are `LiftApp` (`@HiltAndroidApp`, which also plants the Timber trees), `MainActivity`
(`@AndroidEntryPoint`), and `hiltViewModel()` in the screens.

## Coroutines and Flow

**Dispatchers are always injected**, never named at the call site — the qualifiers
[`@IODispatcher`, `@DefaultDispatcher`, `@MainDispatcher`, `@MainDispatcher.Immediate`][dispatchers]
are all declared in `:domain`. Reads expose cold `Flow`s with `.flowOn(dispatcher)`; writes wrap the
work in `withContext(dispatcher)`; anything destructive that must not be interrupted halfway adds
`NonCancellable`.

## The design system

`:ui` is a custom design system over Material 3, its components named `LiftApp*` — `LiftAppButton`,
`LiftAppCard`, `LiftAppScaffold`, `LiftAppTextField`, `LiftAppText`. Reach for those before a raw
Material widget. [`LiftAppTheme`][theme] wraps `MaterialTheme` and provides, through
`CompositionLocal`:

- **`Dimens`** — the spacing and sizing tokens, with separate portrait and landscape sets.
- **A custom [`ColorScheme`][colors]**, distinct from Material's and read through the `colorScheme`
  accessor. Everything the app draws itself uses this one.
- **`ScaleIndication`** as `LocalIndication`, so a press scales rather than ripples.
- **Vico's theme**, derived from the same palette so charts match.

Material's own scheme is still filled in — every role, including ones nothing reads — because the
Material components still in use (the switch, the pickers, the snackbar, the text fields) resolve
their colors from `MaterialTheme.colorScheme` and cannot see the app's palette. Leaving a role unset
lets a component fall back to Material's baseline purple.

Two variable typefaces, each a single `.ttf` whose weight axis is set through the `variableFont()`
helper in [`Typography.kt`][typography]: **Google Sans Flex** for the display, headline, and title
styles, **Inter** for body and label. Add a weight as another `variableFont(...)` entry, not another
font file. There is no mono family — the rest timer aligns its digits with
`fontFeatureSettings = "tnum"` rather than reaching for one. `SinDivider` and `BottomSinShape` carry
the app's sine-wave motif.

`:core` adds what needs a domain model or an app-level dependency: the Vico chart integration, the
formatters behind `LocalFormatter`, composed components (app bars, bottom sheets, steppers, the
search bar, the input pickers, the muscle image), the `TextFieldState` form system, Glide image
loading, and most of the Hilt wiring.

[nia]: https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md
[jvm]: ../build-logic/convention/src/main/kotlin/liftapp.jvm.library.gradle.kts
[lib]: ../build-logic/convention/src/main/kotlin/liftapp.android.library.gradle.kts
[compose]: ../build-logic/convention/src/main/kotlin/liftapp.android.compose.gradle.kts
[feature]: ../build-logic/convention/src/main/kotlin/liftapp.android.feature.gradle.kts
[func]: ../build-logic/convention/src/main/kotlin/liftapp.android.functionality.gradle.kts
[app]: ../build-logic/convention/src/main/kotlin/liftapp.android.application.gradle.kts
[conventions]: ../build-logic/convention/src/main/kotlin/Conventions.kt
[unit]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/unit/ValueUnit.kt
[database]: ../functionality/database/src/main/kotlin/com/patrykandpatrick/liftapp/functionality/database/Database.kt
[migration]: ../functionality/database/src/main/kotlin/com/patrykandpatrick/liftapp/functionality/database/migration
[callback]: ../functionality/database/src/main/kotlin/com/patrykandpatrick/liftapp/functionality/database/DatabaseCallback.kt
[preference]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/datastore/Preference.kt
[type]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/backup/BackupDataType.kt
[loadable]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/model/Loadable.kt
[textfield]: ../core/src/main/kotlin/com/patrykandpatrick/liftapp/core/text/TextFieldState.kt
[previews]: ../core/src/main/kotlin/com/patrykandpatrick/liftapp/core/preview/Previews.kt
[routes]: ../navigation/src/main/kotlin/com/patrykandpatrick/liftapp/navigation/Routes.kt
[root]: ../app/src/main/kotlin/com/patrykandpatrick/liftapp/ui/Root.kt
[commander]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/navigation/NavigationCommander.kt
[deeplink]: ../core/src/main/kotlin/com/patrykandpatrick/liftapp/core/deeplink/DeepLink.kt
[bottombar]: ../app/src/main/kotlin/com/patrykandpatrick/liftapp/navigation/BottomAppBarNavigator.kt
[summary]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exerciseset/summary/SummaryModule.kt
[dispatchers]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/di/CoroutineDispatcherQualifiers.kt
[theme]: ../ui/src/main/kotlin/com/patrykandpatrick/liftapp/ui/theme/Theme.kt
[colors]: ../ui/src/main/kotlin/com/patrykandpatrick/liftapp/ui/theme/ColorScheme.kt
[typography]: ../ui/src/main/kotlin/com/patrykandpatrick/liftapp/ui/theme/Typography.kt
