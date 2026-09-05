# Workout programming — implementation roadmap, phases 2–7

## Status

**Every phase has shipped.** The loop is closed: classified exercises, estimation math, volume
per muscle both logged and planned, prescription authored in the plan and shown during the
workout, mesocycles with deloads, and load suggestions with their reasons.

| Phase | State |
| --- | --- |
| 2 — Classification, progression metadata, shoulder split | **Done**, `ec711df0`, schema 14 |
| 3 — Estimation math | **Done**, `ed1476b5`, no schema |
| 4 — Weekly volume per muscle (actual) | **Done**, no schema |
| 5a — Data model swap | **Done**, schema 15 |
| 5b — Authoring and generation | **Done**, no schema |
| 5c — Show the target during the workout | **Done**, schema 16 |
| 6 — Mesocycle, deloads, planned volume | **Done**, no schema |
| 7 — Autoregulation and stalls | **Done**, no schema |

Each shipped phase keeps its section below, with what actually happened recorded at the top of it.
The plan is left as written underneath: where the two disagree, the note is what is true.

## Context

[workout-programming.md](workout-programming.md) is the reference for how LiftApp should prescribe
work. Its Part 5 lists eight gaps "in rough dependency order"; **items 1, 5 and 8 have shipped**, in
`716e0ce6` and `ec711df0`. This roadmap covers everything left: the math in Part 4 (shipped as
Phase 3, and not itself a Part 5 item), the progression mechanics in Part 3, and items 2, 3, 4, 6
and 7.

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

> **Shipped** in `ec711df0` as schema 14, as planned. All 101 seeded exercises were classified by
> hand — 13 primary compounds, 38 secondary, 39 isolation, null for the eleven `Cardio` and `Time`
> entries. The split also added `Obliques` and `Serratus`, which the plan below does not mention,
> and corrected two mis-annotations beyond the `Shrugs` one anticipated here: the two cable hip
> machines each named the group the other works.

**Schema 13 → 14.** Items 5 and 8, plus the `Muscle.Shoulders` split from Part 4. First because it
has the longest lead time — ~250 authored defaults — and because Phase 5's target generator and
Phase 7's load guards both depend on it.

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

> **Shipped** in `ed1476b5`, no schema. Four things worth carrying forward, three of which this plan
> did not anticipate:
>
> - **Part 4's percentage table is the *inverse* curve** — each cell is `loadFor` against a 100 kg
>   e1RM, not the percentage a 100 kg set implies. Read forwards it misses by up to 3.3 points at
>   20 reps; read as the inverse all 36 cells reproduce exactly. It is now the acceptance test, and
>   the reference says which direction it runs in.
> - **The floor is 20 kg**, and it is a judgement call rather than a derived value. This plan's
>   "plausible band" figures (~47 kg at 10 reps, ~87 kg at 12) would have left the new curve
>   applying to almost nothing; 53 kg is a hard ceiling, being the table's lightest cell, which has
>   to stay on the weight-dependent curve for the acceptance test to mean anything. The curve
>   degrades continuously, so there is no boundary to find — 20 kg is an equipment landmark that
>   catches the 10 kg lateral raise this plan names.
> - **The two curves do not meet**, crossing near 100 kg, so the floor is a step and not a seam. The
>   step is upward at every rep count, so the estimate stays monotonic in load; `loadFor` returns
>   the floor for the band of one-rep maxes that no load reaches. Both are asserted.
> - **`./gradlew testDebug` was not running `:domain`'s tests at all.** A JVM module's test task is
>   `test`, so the name CI uses — and that [AGENTS.md](../AGENTS.md) calls "all unit tests" —
>   matched nothing in the module this phase lives in. Aliased in the root build; see
>   [Cross-cutting](#cross-cutting).
>
> Beyond the plan: the exercise chart now folds in a recorded RIR, so a set of 8 at 2 RIR plots as a
> 10-rep max. A null RIR keeps the old reading, so sets logged before Phase 1 are untouched. The
> mapper had no test and now has one.

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

> **Shipped**, no schema. Eight things worth carrying forward; the sixth and seventh move it off
> the dashboard the plan below puts it on:
>
> - **The accumulator is `MuscleVolume` in `:domain`**, not a `Map<Muscle, Double>` on the
>   statistics. Phase 6 has to fold planned volume the same way for the two to be comparable, and a
>   bare map would have meant reimplementing the fractional credits next to the plan. It also owns
>   the rounding: `bandOf` reads the *rounded* set count, so the number on screen and the band
>   beside it can never disagree — 5.75 sets reads as six sets at maintenance, not as six sets
>   below it. Work too slight to round up to one reads as *under 1 set* rather than as none.
> - **A muscle named in two tiers is credited once, at the highest.** Not anticipated here, and it
>   matters: the annotations are hand-authored and a few name the same muscle twice. Summing them
>   would have paid 1.5× for an authoring slip.
> - **Cardio is excluded from the set count.** The landmarks count working sets of resistance
>   training; a five-kilometre run is not one set of quadriceps, and counting it as one would put
>   every runner over the ceiling on the legs. `Time` exercises are counted — a plank is a set.
> - **The adaptive band starts at 10 sets, not 8.** MEV is a range (~8–10) rather than a line, so
>   the band boundary takes its top: a muscle only reads as productive once it clearly is, and 10 is
>   also ACSM's summary figure. Four bands on three boundaries — 6 / 10 / 20 — and every one of
>   them is a landmark the reference names, none invented to make the bands even.
> - **The calisthenics fix was wider than "volume".** Both chart mappers read the added weight
>   alone, so an unweighted pull-up plotted a one-rep max of zero while the statistics card above it
>   showed the body weight it moved. `ExerciseSet.totalWeight` is now the single reading, and the
>   dashboard's tonnage — a third layer, which this plan does not mention — was counting the added
>   weight alone too.
> - **It did not stay in the dashboard fold, and the dashboard is not where it lives.** The window
>   is the rolling seven days, not the calendar week the rest of the dashboard totals cover: a
>   Monday morning would otherwise report an almost empty week, and Sunday's work would drop out
>   overnight. Two windows means two queries, so the fold is `GetMuscleVolumeUseCase` in `:domain`,
>   the table is `:feature:musclevolume` — where the window is selectable, following
>   `:feature:exercisedetails` — and the dashboard keeps a card of the three hardest-worked
>   muscles, which is a way in rather than the picture. The row and the legend live in `:core`, so
>   the card and the screen draw the same thing.
> - **A window longer than a week is divided down to one.** Every landmark is stated per week, so a
>   month's total is a number nothing can be compared against. `MuscleVolume.perWeek` restates it
>   as a weekly average and the caption says which of the two is on show. There is no yearly
>   option: a year averaged to a week smooths away every block, deload and layoff that made it.
>
> - **A scale with a thumb, not a bar to fill.** A filled bar is a claim about direction: more is
>   nearer to done, and full is the goal. That is the wrong shape for this quantity — the
>   productive band is in the middle and the top of the range is a limit — so `LiftAppScaleBar`
>   draws the whole scale as a muted gradient and only the thumb moves. It also makes the
>   landmarks visible, where the old bar left them to the legend. The track runs a quarter past
>   MRV so the top band has width instead of being a point every over-reaching muscle pins to,
>   each boundary crossfades over one set rather than a knife edge, and the track is drawn well
>   back so a column of rows is not fifteen rainbows and the thumb is not lost on its own colour.
>   The landmarks — 6, 10 and 20 — are dots on every bar rather than labels on a strip above the
>   column: a strip is only beside the first row or two, and by the tenth the reader is sighting up
>   an imaginary line or has scrolled it away. A dot is a position, and a position only needs
>   naming once, so the set counts live in the key beside the band names — above the rows rather
>   than below them, since a key that follows what it decodes is one the reader meets too late.
>   The card carries the counts without the names, on one line: the names do not fit across a
>   card's narrower measure without being cut, and a card is only being asked what the colours are
>   worth. That the key holds a fixed number of entries to a row is not cosmetic — a card sizes
>   itself on what its content says it needs, and a freely wrapping flow answers that question
>   differently depending on the width it is asked about, which left the card a line short and its
>   button squeezed to nothing. Those counts are
>   written over whole sets — 6–9, 10–19, 20+ — because whole sets are what the rows show, and
>   "6–10" would put a row reading ten sets in two bands at once.
>
> Beyond the plan: `TotalVolumeExerciseSetSummaryMapper` had no test and now has one.
>
> **The bands do not use the theme's semantic colors.** Read in band order, grey / yellow / green /
> red reverses direction around the color wheel twice, so a gradient between them crosses the middle
> of the wheel where there is no color left, and the crossfades come out olive and brown. The scale
> sweeps slate → sky → emerald → amber instead, which only ever turns one way, and it is drawn by
> rotating hue at held chroma rather than cutting straight across — worth up to half again as much
> chroma at the midpoint of a blend. Two things fell out of getting that right: the track is muted
> by taking its chroma away rather than its alpha, because backing a color off toward a dark
> background turns the warm end brown (a dark amber *is* brown), and it is opaque, so a row looks
> the same on the dashboard's card as on the screen behind it.

**No schema.** Part 4's volume accounting applied to *logged* work. Planned volume needs the targets
from Phase 5 and lands in Phase 6.

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

> **5a landed, schema 15.** `SetTarget` and `ExerciseTarget` in `:domain`, an `exercise_target`
> table keyed by `(plan, cycle, day, exercise)` with the per-set list as JSON, and `Goal` — both
> domain types minus `Workout.Goal`, the `goal` table, `exercise.exercise_goal`, and
> `:feature:exercisegoal` — gone. The routine screen no longer shows a prescription, which is the
> one visible change 5a makes.
>
> Three things the plan below did not anticipate:
>
> - **The legacy migration had to be frozen first.** `Migration11To12` writes the version 12 schema,
>   which *has* `goal` and `exercise_goal`, so it cannot follow a domain type that no longer exists.
>   `LegacyGoal` in the migration package is that shape pinned, and it is what `Migration14To15`
>   reads back. A migration that tracks the live domain rewrites history every time the domain moves.
> - **`plan_item_schedule` grew `cycle_index` and `item_order_index`.** Targets are keyed by where a
>   day falls in the plan, so starting a workout has to turn "today" into that key. Deriving it from
>   the active plan's start date would have made prescription depend on a preference the schedule
>   rows do not; storing it on the row it describes does not.
> - **`workout_goal` is still flat, so the snapshot loses per-set differences.** It takes the widest
>   range any set asks for. That is invisible while 5a generates uniform sets and stops being true
>   the moment 5b ramps them — 5c is where per-set targets reach the screen and the snapshot has to
>   grow with them.
>
> `SupersetConfig` no longer overrides a per-exercise prescription, because there is none on a
> routine to override: it stays the superset's own set count and rest time. `UpsertGoalSetsUseCase`
> was left alone — it edits `Workout.Goal`, which survives.

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

> **Landed in two parts; this note covers both.** `TrainingIntent`, `TargetPrescriptions` — the Part 2
> tables as one lookup, read by both the generator and the out-of-plan defaults so the two cannot
> disagree — and `TargetGenerator`, which writes one target per (cycle, day, exercise). Activation
> runs it: `SetActivePlanUseCase` now takes `GenerationParameters` and re-authors the whole plan,
> and `PlanConfiguratorScreen` grew the intent picker and the starting-RIR input. 23 unit tests.
>
> Two ramps, per Phase 6's note that both are generator defaults rather than new data. **RIR** goes
> evenly from the declared start down to the floor of the role's band, so four cycles from 3 read
> 3, 2, 2, 1; a start already at or below the floor is left alone rather than raised to give the
> ramp somewhere to fall from. **Set count** climbs linearly to roughly double by the last cycle.
>
> The volume ramp is the one place the translation is lossy. The reference states MEV → MRV *per
> muscle*; the generator applies it per exercise. Doubling every exercise does double each muscle,
> so the shape survives — what it cannot do is notice that two exercises share one. Phase 6's fold
> over planned volume is what would.
>
> **The editor** is `PlanTargetsScreen`, keyed by (plan, cycle, day) and reached from a Targets
> button on every day of `ActivePlanScreen` — every day, not just today's, because authoring per
> cycle is for changing what a day will ask for before it arrives. It lists each exercise with its
> sets spelled out rather than summarised, and a sheet edits the set count, the rep range, the RIR
> and the rest time.
>
> **It edits the exercise, not the set.** That is the shape the generator writes and the shape a
> lifter changes — "make this exercise fives this week", not "make set three fives" — so saving
> levels any difference between an exercise's sets. Nothing can author such a difference yet; when
> something can, this is where it has to grow.
>
> **Activation no longer re-authors by default.** It fills gaps only, so a plan reconfigured to
> start on a different date keeps every edited target, and a plan that gained a day or an exercise
> still gets a prescription for it. A checkbox on the configurator asks for the full rebuild that
> changing the intent or the starting RIR implies. A silent rebuild is indistinguishable from having
> lost the edits to a bug, which is why this is a question rather than a default.
>
> `TrainingIntent` is deliberately *not* on `ActivePlan`: the targets are materialised, so nothing
> downstream needs it, and leaving it off keeps the DataStore JSON — which
> `LegacySharedPreferencesMigrationTest` asserts exactly — untouched.

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

> **Landed, schema 16.** The snapshot got the shape the target already had. `workout_goal` held one
> rep range and a count; it now holds a `List<SetTarget>`, and `Workout.Goal` reads its set count,
> rep range and duration off that list so the two can never disagree. The sheet that logs a set
> shows what that set was asked for, above the fields recording what it was.
>
> Part 3's note — that the last set lands closer to failure than the first — is shown only for
> straight sets, where every target is identical. That is the one shape where it is true: a ramped
> or backed-off prescription has already accounted for the drift, and saying it there would be
> wrong.
>
> Two things worth knowing:
>
> - **The column was renamed, not just retyped.** `workout_goal_sets` held a count; the new one is
>   `workout_goal_set_targets`. A restore matches archive columns to table columns by name and skips
>   what it does not recognise, so keeping the name would have fed a pre-16 archive's `3` to a JSON
>   parser. Renamed, the stale column is ignored and the new one takes its `'[]'` default — the old
>   session keeps its logged sets and loses only the target it was measured against.
> - **A set added mid-session has no target**, and the card is absent rather than empty: a blank
>   target card reads as a prescription of zero.

`:feature:workout` consumes the goal only as behaviour — `goal.restTime` for the timer, `goal.sets`
for how many empty rows. **`minReps`/`maxReps` never reach the screen.** So this is new UI, not
edited formatting: show the target next to the set being logged, plus Part 3's note that the last set
will be closer to failure than the first.

**Verify:** migration test asserting existing goals became equivalent targets; a plan round-trip
(create → activate → edit a cell → start a workout → confirm the target shown matches); `.lfa` export
and restore.

---

## Phase 6 — Mesocycle, deloads, planned volume

> **Done, no schema.** The schedule table now holds the days of the plan being run and nothing
> else: activation replaces it rather than appending, and both readers name the plan they want,
> because a restored archive arrives without passing through activation. `PlanSchedule` answers
> every cycle-position question that was written out three times, and says a date outside the plan
> has no position rather than an extrapolated one.
>
> Deloads are a block length on the plan. The ramps read the block rather than the whole plan, so a
> second block starts over at the top of the RIR range instead of continuing down from where the
> first ended — the gap 5b left. The deload halves the volume the week before it carried and backs
> RIR well off failure, keeping the days. Note the reference disagrees with itself here: the stated
> rule is "cut sets by roughly half", while its worked example ramps 10 → 20 and deloads to about 6
> rather than 10. The rule won.
>
> **Planned volume** is the same per-muscle accumulator as logged volume, folded over the active
> plan's targets for the cycle a date falls in, and shown beside the actual figure on the volume
> screen. It needed one thing logged volume does not have: `perWeekOfCycle` scales *up* as well as
> down, because a five-day cycle run back to back really does deliver its sets seven-fifths as
> often as a week. `perWeek` refuses to scale up for exactly the opposite reason — a short window
> of logged work is a count that happened.
>
> One thing found while verifying this phase and fixed separately in `afdca431`: **CI was running
> 161 of the repo's 382 tests.** An Android module has no `testDebug` task, only
> `testDebugUnitTest`; Gradle abbreviates a qualified task name so `:module:testDebug` resolved and
> hid it, while the unqualified `./gradlew testDebug` that CI runs matched only the JVM alias.
>
> **Moved ahead of autoregulation.** This was Phase 7 and is now Phase 6, because two of its items
> are foundations the other phase would build on rather than follow. Cycle-position arithmetic is
> written three times already and autoregulation adds a fourth reader of it. The two plan bugs are
> worse: `getPlanPosition` in Phase 5 had to be scoped to a plan precisely because
> `insertPlanItemSchedule` appends overlapping schedule rows, and that workaround should come out
> with the cause rather than be joined by more of its kind. Doing this first also means planned
> volume exists before anything starts suggesting changes to it.

With per-cycle targets authored in Phase 5, the RIR ramp (3 → 2 → 1 → 0–1) and the volume ramp are
**generator defaults**, not new data. What remains:

- **Deload weeks** — cut sets roughly in half, keep frequency, reduce load moderately and raise RIR
  by 2–3, never stop entirely. A flag on the cycle, driving generation.
- **Planned volume per muscle** — the Part 4 fold over the active plan's targets, reusing Phase 4's
  per-muscle accumulator so planned and actual are directly comparable.
- **Unify cycle-position arithmetic.** `daysBetween / itemCount` and `% itemCount` are implemented
  **three times** — `GetPlanStateUseCase`, `DashboardViewModel.getNextRoutineID`, and
  `PlanState.getAllCycleDates`. Collapse into one `:domain` helper before adding a second dimension.
  Phase 7 reads the same arithmetic to place a session in its block, so this lands first.
- **Two pre-existing plan bugs that get worse**, and should be fixed before or with this phase:
  `insertPlanItemSchedule` is a bare `@Insert` with no prior delete, so re-activating or switching
  plans appends overlapping rows; and `getScheduledRoutine` filters by date without
  `plan_item_schedule_plan_id`, so the mapper then picks arbitrarily among them. `TargetDao
  .getPlanPosition` takes a plan for the same reason and can stop doing so once the duplicate rows
  cannot exist.

`ActivePlan` is JSON in DataStore, so anything added there needs **no migration** — but
`LegacySharedPreferencesMigrationTest` asserts its exact JSON string and will need updating.

---

## Phase 7 — Autoregulation and stalls

> **Done, no schema.** `SuggestLoadUseCase` is the reference's loop, and the three guards are
> enforced inside it rather than left to callers — a caller cannot obtain a suggestion without its
> reason, because `LoadSuggestion` cannot be built without one.
>
> Two things the plan did not say, both found by the tests:
>
> - **A regression is not a stall.** Going backwards has an answer already — the back-off — and
>   treating it as a stall escalated to questioning the effort and then deloading over what one
>   back-off handles. A stall is standing *still*, and only that.
> - **An ordinary double progression over a wide rep range meets the ten-percent guard every time.**
>   Dropping from twelve reps to eight at the same effort is worth more than a tenth of the bar on
>   its own, so over 8–12 the guard fires on the honest arithmetic rather than only on an outlier.
>   That is the guard working, and it is why the capped reason carries what was asked for: a user
>   who sees the same +10% three weeks running deserves to know the loop wanted more.
>
> Stall escalation needs no stored state — the step is read off how many sessions the stall has
> persisted, so cheapest-first falls out of the history rather than out of a counter someone has to
> keep. Changing the exercise is reachable only after the three cheaper answers.
>
> **Not wired to a screen.** The loop and its guards are done and tested; nothing shows a suggestion
> to a user yet. Where it belongs is the weight field of the set being logged, and what it says
> there is `SuggestionReason` turned into a sentence with the resources.
>
> **Moved behind the mesocycle work**, which was Phase 6 — see the note there. Its inputs were all
> in place from Phase 5 onward; it waited on the other only for the cycle arithmetic and the plan
> bugs it would otherwise have inherited.

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

`testDebug` now reaches the JVM modules too. It did not before Phase 3 — the root build registers it
as an alias for `test` there — so a test added to `:domain` before that commit was never run by CI.
Worth knowing when reading a green run from further back.

[arxiv]: https://arxiv.org/abs/2603.17495
