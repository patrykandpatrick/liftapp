# Workout programming — implementation roadmap, phases 2–7

## Context

[workout-programming.md](workout-programming.md) is the reference for how LiftApp should prescribe
work. Its Part 5 lists eight gaps "in rough dependency order"; **item 1 (RIR on `ExerciseSet`)
shipped** in `716e0ce6`. This roadmap covers everything left: the math in Part 4, the progression
mechanics in Part 3, and items 2–8.

One decision reshapes the rest. The reference assumes `Goal` stays where it is and grows fields.
Instead, **prescription moves into the plan**: exercises stop carrying goals, and targets are
authored per set × per exercise × per plan cycle. That makes the mesocycle (Part 3's RIR ramp and
volume ramp) *authored data* rather than a derived function, which is both simpler and fully
user-editable — and it deletes the `exercise_goal` column that today gives all 101 seeded exercises
the same meaningless `8–12 reps × 3 sets`.

### Decisions already taken

| Question | Decision |
| --- | --- |
| Per-exercise metadata | Role **and** progression policy + load increment, classified first (Phase 2) |
| `Muscle.Shoulders` | Split into front/side/rear delts in Phase 2 |
| e1RM formula | Hybrid: weight-dependent above a load floor, Epley below (Phase 3) |
| Where targets live | The plan is the **only** source. Drop `exercise.exercise_goal` **and** the `goal` table |
| Target grain | Per set × per exercise × per plan cycle (week), user-editable |
| Materialisation | Generate and store every target at plan creation; identity as columns, the per-set list as one JSON payload |
| Training intent | A plan-generation parameter, not a column on `Routine` |

**Deferred:** per-exercise fatigue cost (item 7). It only ever changed a warning, and needs 101
genuine judgement calls. It can be added later as one more column in the same shape as Phase 2.

---

## Phase 2 — Exercise classification, progression metadata, and the shoulder split

**Schema 13 → 14.** Items 5 and 8, plus the `Muscle.Shoulders` split from Part 4. First because it
has the longest lead time — ~250 authored defaults — and because Phase 5's target generator and
Phase 6's load guards both depend on it.

All three land together: they touch the same table, the same authoring pass over
`DefaultExercises.kt`, and the same data migration. Splitting them would pay that cost three times.

### Fields

Add to [`Exercise`](../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exercise/Exercise.kt)
and `ExerciseEntity`:

| Field | Values | Notes |
| --- | --- | --- |
| `role` | `PrimaryCompound` / `SecondaryCompound` / `Isolation`, **nullable** | Indexes the Part 2 rep-range and RIR tables |
| `progressionPolicy` | `DoubleProgression` / `FixedIncrement` / `RepsOnly` | Small-load exercises must default to `RepsOnly` — a 2 kg jump on a 10 kg lateral raise is a 20% increase |
| `loadIncrement` | nullable number | The equipment step, feeding `roundToIncrement` and the "refuse a jump over ~10% of current load" guard |

**Role is orthogonal to `ExerciseType`, not derived from it.** Part 2's table has a fourth row,
"Bodyweight-limited (`Calisthenics`, `Reps`)", which is selected by type at lookup time rather than
stored — but a weighted pull-up is `Calisthenics` *and* a primary compound, so the two axes must stay
independent. Storing three roles and letting `ExerciseType` supply the bodyweight-limited row avoids
encoding the same fact twice.

**Role is nullable** because `Cardio` and `Time` exercises genuinely have none, and because null is
the honest value for a user-created exercise nobody has classified. This follows the precedent Phase
1 set with `rir`: null means "not applicable or not stated", never a silent default. Everything
downstream must degrade gracefully on null rather than assume a role.

### Can muscle lists infer role? — as a suggestion, not as a value

Measured against the 101 seeded exercises (90 load-bearing, hand-labelled). Total muscle count across
all three tiers gets **isolation vs compound right 84%** of the time — clearly better than the
`primaryMuscles.size` proxy the reference rejects by name, which manages 67%.

But the three-way split we need scores **70%, and identifies only 3 of 15 primary compounds** — the
class driving the most consequential prescriptions (3–5 reps, 1–2 RIR, 3 min rest). The failure is
systematic, not random:

| Exercise | Total muscles | Guess | Actual |
| --- | --- | --- | --- |
| `GoodMornings` | 1 | Isolation | Secondary compound |
| `LegPress` | 2 | Isolation | Secondary compound |
| `LatPulldown` | 3 | Isolation | Secondary compound |
| `BarbellRows` | 4 | Compound ✓ | Secondary compound |
| `LegRaise` | 6 | Compound | Isolation |

`LatPulldown` and `BarbellRows` are the same movement pattern; the heuristic separates them on **how
thoroughly each was annotated**. Those lists were authored to drive the muscle diagram, not to
classify load, so their granularity tracks the author's thoroughness. The four ab exercises fail in
reverse — six muscles each, because ab work recruits stabilisers.

So use it for **pre-selection, never persistence**:

- Pre-select the guess in the editor picker, so classifying a new exercise is one tap of confirmation.
- Run it as a first pass over the seeded 101, turning the authoring job into "review and correct
  ~16 of 90" rather than "decide 90 from scratch".
- Offer it as a suggestion in the Unclassified bucket, never write it silently.

Persisting a 70%-accurate guess would have the app confidently prescribe 3–5 reps at 2 RIR for a
lateral raise, and would erase the difference between "we guessed" and "the user said" — the same
reasoning that made `rir` nullable in Phase 1.

### The seeded catalog

`DefaultExercises.kt` is 911 lines of 101 `Exercise.Insert(...)` entries. Type distribution is
Weight 73, Reps 13, Time 8, Calisthenics 4, Cardio 3 — so roughly 90 entries need a genuine role
judgement, and the 11 `Cardio`/`Time` entries get null. Progression policy and load increment fall
out of type and equipment for most entries. Giving all three fields Kotlin defaults on
`Exercise.Insert` means the file compiles untouched and each entry gains named arguments in place.

Four things to get right:

1. **`DefaultExercises.kt` is append-only.** IDs 1–101 match the published app and the legacy
   migration relies on that correspondence. Add named arguments per entry; never reorder or insert
   into the middle.
2. **A `DEFAULT` in the migration only covers existing rows with a single value.** The 101 seeded
   rows on an upgrading device were inserted at v12 and would all collapse to that default. Give
   upgraders the real values with a data migration that `UPDATE`s rows 1–101 by ID, generated from
   the same list `DefaultExercises` uses. This is larger than the schema change and is the easy part
   to miss.
3. **In `Migration11To12.seedDefaults`, the frozen part is the *column list*, not the values.** It
   calls `json.encodeToString(exercise.mainMuscles)` over `DefaultExercises` at compile time, so
   editing the seed file changes what that migration writes. Keep its seven-column INSERT exactly as
   is — `runMigrationsAndValidate` at v12 depends on it — but expect it to start emitting the new
   muscle constants, and check `Migration11To12Test`'s assertions. The same applies to
   `LegacyDatabaseWriter.restoreExercise`. **The consequence: the new migration must be idempotent**,
   because a fresh 11 → 12 → 13 → 14 chain will present rows that already hold the new values.
4. Store enums as TEXT via Room's built-in converter, matching every other enum column
   (`exercise_type`, `routine_item_type`, …). **Constant names become the persisted format** —
   renaming one is a silent data break.

### User-created exercises

The seeded 101 are only half the problem.

- **New ones** — add role and progression policy pickers to `NewExerciseScreen`. Two more
  `ListTextField` call sites (~8 lines each) following the `UpdateExerciseType` action template,
  plus `prettyName`/`nameRes` extensions matching `ExerciseTypeResources.kt`. Pre-select the
  muscle-count guess (above), refined by `ExerciseType`, so the common case is a confirmation.
- **Existing ones on upgrade** — they stay `NULL`. The guess is good enough to offer and not good
  enough to store; a name-substring heuristic ("curl", "fly", "raise") is worse still, being fragile
  and English-only.
- **Getting them classified** — add `Role` to `GroupBy` in the exercises tab. The chip row already
  iterates `GroupBy.entries`, so this is a `when` branch and a string, and it surfaces an
  "Unclassified" bucket the user can work through at their own pace, each row pre-filled with the
  suggestion. Cheaper and less annoying than a prompt, and it doubles as the audit view for the
  seeded defaults.

### Splitting `Muscle.Shoulders`

Part 4: "Front delts receive heavy indirect volume from every press while side and rear delts receive
almost none, so aggregate shoulder volume is the least trustworthy row in that table." Replace
`Shoulders` with `FrontDelts`, `SideDelts`, `RearDelts`.

**53 of the 101 seeded exercises touch `Shoulders`** — 16 at primary tier, 26 secondary, 11 tertiary.
The primary-tier ones are mostly unambiguous (`OHP` → front, `SideArmRaise` → side,
`RearDeltRaise`/`FacePull` → rear), and the pass will surface a few mis-annotations worth fixing —
`Shrugs` currently lists shoulders as primary where traps would be right.

Three compatibility surfaces, only one of them dangerous:

1. **Rows already in the database** — the migration rewrites the three JSON columns on `exercise`:
   rows 1–101 from the hand-authored mapping, everything else `Shoulders` → `FrontDelts`. Must be
   idempotent, per the note above.
2. **Old `.lfa` archives restored *after* the upgrade — the real hazard.** `SqliteCsv` replays raw
   strings into the column at any later date, so the migration can never reach them, and
   `DomainModule.provideJson` sets neither `ignoreUnknownKeys` nor `coerceInputValues` — decoding
   `"Shoulders"` **throws**. That is a crash, not a degradation. Fix it with a tolerant `List<Muscle>`
   serializer in `JsonConverters` (the single place these columns are (de)serialized): decode through
   a legacy alias table, drop genuinely unknown names with a Timber warning rather than throwing, and
   only ever encode current names.
3. **The bitmap cache** — bump `NameInfo.VERSION`, which invalidates every cached image by design.
   New drawables land in `ResourceBitmapProviderImpl`'s `frontDrawableRes` / `rearDrawableRes`, and
   `RearDelts` must join `Muscle.isRearMuscle` in `MuscleBitmapGeneratorImpl` — those accessors
   `error(...)` for the wrong side, so a missing entry is a crash.

`Shoulders` decodes to **`FrontDelts`**: every press loads front delts, so it is the
highest-probability single answer, and it under-claims side and rear — the direction Part 4 wants.
Mapping to all three would triple the volume and rebuild the exact lump the split exists to remove.

Also needed: `muscle_front_delts` / `_side_delts` / `_rear_delts` strings plus their
`_list_continuation` forms, and the `MuscleModel` / `MuscleTypeResources` mappings.

**Fix `Exercise.Update` while in there.** It has no `exerciseType`, so the edit screen shows a type
dropdown whose value is silently dropped on save. Any new editable field must reach `Exercise.Update`,
`ExerciseEntity.Update` and `StateToExerciseUpdateMapper` — you will walk past this bug on the way.

**Verify:** `Migration13To14Test` copying `Migration12To13Test`. Assert an upgraded database has
per-exercise values rather than a uniform default; that a user-created row is left unclassified; that
`Shoulders` became the right delt per exercise; and that **running the migration twice is a no-op**.
Round-trip a `.lfa` exported before the change — restore silently drops unknown *columns*, and the
tolerant serializer must absorb the unknown *values*, so confirm an old archive imports without
throwing and lands on `FrontDelts`. Check the muscle diagram renders for an exercise of each delt.

---

## Phase 3 — Estimation math

**No schema. Pure `:domain`.** Independent of Phase 2; either order works.

[`OneRepMaxCalculator`](../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exerciseset/OneRepMaxCalculator.kt)
is 11 lines of Epley with three callers and **no dedicated test** — the one existing test asserts
against the calculator itself, so it cannot catch a formula change. Write that test first.

Add, all in the same object:

- `estimatedOneRepMax(weight, reps, rir)` — Part 4's one-liner, `oneRepMax(weight, reps + rir)`.
- `loadFor(oneRepMax, reps, rir)` — the inverse. Nothing in the repo inverts today.
- `roundToIncrement(load, increment)` — "a suggestion of 62.4 kg is worse than 62.5 kg". Takes the
  per-exercise `loadIncrement` from Phase 2.

**On the formula.** The paper ([arXiv 2603.17495][arxiv]) exists and its equation matches Part 4. Two
findings the reference does not record:

- **`w` is in kilograms.** Reproducing Part 4's own table gives 1.13 pts mean error reading `w` as kg
  versus 3.76 pts as lb, and its "~2–3 points lower at 60 kg, ~2–3 higher at 200 kg" claim reproduces
  exactly (−2.7 / +2.9). Epley is scale-invariant, so there is **no unit bug today** — the `ln(w)`
  term introduces one. Callers currently pass `unitConverter.convertToPreferredUnit(...)` output,
  i.e. the *user's* unit. Normalise to kg inside the calculator and convert back, or take the unit as
  a parameter.
- **It collapses at light loads.** The divisor `-2.55 + 4.58·ln(w)` reaches zero at w ≈ 1.745 kg and
  is unreliable well above that. A 10 kg lateral raise for 12 reps implies the 12RM is 51% of 1RM
  against Part 4's own table value of 69%; the estimate only enters a plausible band above ~47 kg at
  10 reps and ~87 kg at 12. That is precisely the light-load, high-rep isolation work Part 2 says
  belongs at 10–15 reps.

So: **normalise to kg, apply the weight-dependent formula above a load floor, fall back to Epley
below it.** Make Part 4's percentage table the acceptance test, and assert the floor behaviour
explicitly — including that the estimate is never below the load itself.

**Verify:** new `OneRepMaxCalculatorTest` in `domain/src/test/`, table-driven against Part 4; check
the three existing call sites still compile and that the 1RM calculator screen and the exercise
details chart still render.

---

## Phase 4 — Weekly volume per muscle (actual)

**No schema.** Part 4's volume accounting applied to *logged* work. Planned volume needs the targets
from Phase 5 and lands in Phase 7.

[`GetDashboardStatisticsUseCase`](../feature/home/src/main/kotlin/com/patrykandpatrick/liftapp/feature/dashboard/model/GetDashboardStatisticsUseCase.kt)
is the only time-windowed aggregation in the app: it takes `today.atStartOfWeek(firstDayOfWeek)`,
queries `GetPastWorkoutsInRangeContract`, and folds `workout.exercises → sets.filter { isCompleted }`.
The exercise and its muscle lists are in scope at the accumulation point, so the change is
`Double` → `Map<Muscle, Double>` plus Part 4's fractional weights (`DIRECT 1.0`, `INDIRECT 0.5`,
`MINOR 0.25`).

Three traps:

- **Do not route this through `ExerciseSummaryType` / the `@IntoMap` multibinding.** That pipeline is
  single-exercise by construction — `ExerciseSetGroup` carries no muscle data and there is no
  cross-exercise variant of `GetExerciseSetsUseCase`.
- **Do not reuse `RoutineMapper.flattenMuscles`.** It is set-union with tier precedence and
  deliberately discards counts — the exact opposite of what volume needs.
- `Workout.Exercise` is **not** a `MuscleContainer` and names its first list `mainMuscles`, not
  `primaryMuscles`.

Present as bands against MV ~6 / MEV ~8–10 / MAV 10–20 / MRV ~20+ sets per muscle per week, never a
decimal. Warn, never block. Because Phase 2 split the deltoids, this is the first version of the
table whose shoulder rows are trustworthy — Part 4's caveat about them no longer applies, except for
exercises a user has not yet reclassified.

Fix while here: calisthenics volume disagrees between layers today —
`GetExerciseStatisticsUseCase` uses `weight + bodyWeight`, the chart mappers use `weight` alone.

**Verify:** unit-test the fold following `GetDashboardStatisticsUseCaseTest`'s fun-interface fake
pattern; check a week of logged sets produces sane per-muscle counts on the emulator. Note nothing in
`core/chart` draws a horizontal threshold band, and `colorScheme.chartColors` has four entries
against 15 muscles — a landmark visual is new work, so consider a list or bar-per-muscle before
reaching for Vico.

---

## Phase 5 — Prescription moves into the plan

**Schema 14 → 15. The big one — split into three landings.**

`Goal` is referenced by **29 files**, has two domain types (`Goal`, `Workout.Goal`) and three storage
sites (`exercise.exercise_goal` JSON, the `goal` table, the `workout_goal` snapshot). Several writers
are hand-written positional SQL: `WorkoutDao.copyRoutineGoalsToWorkoutGoals`,
`LegacyDatabaseWriter.goalValues`, `RoomWorkoutRepository.upsertWorkoutGoal`.

### New model

```
Plan → cycle (week) → plan item (day) → exercise → [SetTarget, SetTarget, …]
```

One row per `(plan, cycleIndex, planItemOrderIndex, exercise)`, ~250 rows for a 6-cycle plan:

| Column | Why |
| --- | --- |
| `plan_id`, `cycle_index`, `plan_item_order_index`, `exercise_id` | Identity — real columns so they are queryable and foreign-keyed |
| `targets` | `List<SetTarget>` as JSON: `minReps`, `maxReps`, `rir`, `duration`, `distance`, `calories`, optional `loadPercent` |
| `rest_time_millis` | Per exercise, not per set |

Identity-as-columns + JSON payload is exactly how `exercise_goal` works today, and `JsonConverters`
already handles `Name`, `Goal`, `List<Muscle>`. Targets are always read and written whole — never
"set 3 across all exercises" — which is the signal that JSON is the right call here. The one
trade-off: set-level fields cannot be aggregated in SQL, so planned-volume folds happen in Kotlin
over ~250 rows. That is fine.

**Set count becomes implicit** — it is the length of the list. That is what makes the volume ramp
(10 → 13 → 16 → 20 sets across weeks) expressible at all.

### 5a — Data model swap, behaviour unchanged

Introduce the target table and `SetTarget`/`ExerciseTarget`; drop `exercise.exercise_goal` and the
`goal` table; retire `:feature:exercisegoal`. Migrate existing plans by generating targets from the
goals being deleted, so nothing regresses visibly. `workout_goal` stays as the per-workout snapshot
but is sourced from the plan. Add app-level defaults for workouts started outside a plan, derived
from Phase 2's role and progression policy — with the `goal` table gone, routines are ordered
exercise lists and the routine screen no longer shows a per-exercise prescription.

Watch: `SupersetConfig` overrides sets and rest time for a whole superset and now overlaps the target
list; `UpsertGoalSetsUseCase` (the ±1 set buttons in the workout) must edit the target instead; and
`Goal.setRange` is used by `SupersetViewModel`.

### 5b — Authoring and generation

Plan creation grows target authoring. The generator takes intent (strength / hypertrophy / both) plus
a starting RIR and ramps, and indexes the Part 2 tables by **(role × intent)** — role from Phase 2,
intent from the plan. Generating every row then storing it mirrors how `addPlanItemsSchedule` already
materialises one `plan_item_schedule` row per date. Every generated cell is editable afterwards, and
a partially-edited plan needs no merge logic.

Exercises with a null role fall back to `ExerciseType`-only defaults — correct but blunter, which is
the incentive for the "Unclassified" bucket in Phase 2.

`PlanCreatorScreen` is today a flat, unsegmented day list (`DayIndicator`, "1 / Day", "2 / Day") — it
has to grow a cycle dimension. `PlanConfiguratorScreen` already speaks in weeks (`lengthWeeks`,
`lengthRemainingDays`) and is the natural home for the generator parameters.

### 5c — Show the target during the workout

`:feature:workout` consumes the goal only as behaviour — `goal.restTime` for the timer, `goal.sets`
for how many empty rows. **`minReps`/`maxReps` never reach the screen.** So this is new UI, not
edited formatting: show the target next to the set being logged, plus Part 3's note that the last set
will be closer to failure than the first.

**Verify:** migration test asserting existing goals became equivalent targets; a plan round-trip
(create → activate → edit a cell → start a workout → confirm the target shown matches); `.lfa` export
and restore.

---

## Phase 6 — Autoregulation and stalls

**Little or no schema.** The loop from Part 5, now that RIR, e1RM, role, increments and targets all
exist. `Workout.Exercise.lastSets` is already populated and surfaced as `previousWorkoutSets` — that
is the feed for "best e1RM in the last N sessions".

Implement the reference's pseudocode, with its three guards non-negotiable:

- Never suggest a jump over ~10% of current load, whatever the arithmetic says.
- Require two data points before acting — one bad session is noise.
- **Show the reason.** "Same weight — you got 9 of 12 last time" is a coaching cue; a number that
  changes silently is a bug report.

Respect the per-exercise progression policy: `RepsOnly` exercises never get a load suggestion, and
every suggested load is snapped with `roundToIncrement`.

Stall detection is two sessions without progress at target RIR, escalating cheapest-first: check the
RIR was honest → deload the exercise ~10% → reduce volume elsewhere for that muscle → change the
exercise. Never suggest changing the exercise first.

Progress load **or** volume in a given week, never both.

**Verify:** table-driven unit tests over synthetic session histories covering load-up, hold, back-off,
and both guard rejections; confirm the reason string is always populated.

---

## Phase 7 — Mesocycle, deloads, planned volume

With per-cycle targets authored in Phase 5, the RIR ramp (3 → 2 → 1 → 0–1) and the volume ramp are
**generator defaults**, not new data. What remains:

- **Deload weeks** — cut sets roughly in half, keep frequency, reduce load moderately and raise RIR
  by 2–3, never stop entirely. A flag on the cycle, driving generation.
- **Planned volume per muscle** — the Part 4 fold over the active plan's targets, reusing Phase 4's
  per-muscle accumulator so planned and actual are directly comparable.
- **Unify cycle-position arithmetic.** `daysBetween / itemCount` and `% itemCount` are implemented
  **three times** — `GetPlanStateUseCase`, `DashboardViewModel.getNextRoutineID`, and
  `PlanState.getAllCycleDates`. Collapse into one `:domain` helper before adding a second dimension.
- **Two pre-existing plan bugs that get worse**, and should be fixed before or with this phase:
  `insertPlanItemSchedule` is a bare `@Insert` with no prior delete, so re-activating or switching
  plans appends overlapping rows; and `getScheduledRoutine` filters by date without
  `plan_item_schedule_plan_id`, so the mapper then picks arbitrarily among them.

`ActivePlan` is JSON in DataStore, so anything added there needs **no migration** — but
`LegacySharedPreferencesMigrationTest` asserts its exact JSON string and will need updating.

---

## Explicitly out of scope

The reference is emphatic about both, and so is this roadmap:

- **No periodization engine.** The 2026 ACSM position stand found complex periodization did not
  consistently affect outcomes. Users who want undulation write different routines into a plan —
  which the model already supports, and Phase 5 makes per-week.
- **No "effective reps" metric.** A plausible mechanism with little direct support; RIR already
  captures the defensible part.

The only deferred item is per-exercise fatigue cost, as noted in Context.

## Cross-cutting

Every schema phase needs a version bump, a migration, an exported JSON, and a migration test
([AGENTS.md](../AGENTS.md)). Backup travels for free — `SqliteCsv` derives headers from the table —
but restoring an older archive writes new columns at their SQL default, so prefer defaults that are
honest about being unknown. Run `./gradlew testDebug ktfmtCheck detektCheck` per phase, and
`./gradlew -p build-logic :convention:ktfmtCheck` if a convention plugin is touched.

[arxiv]: https://arxiv.org/abs/2603.17495
