# Workout programming

This is the reference for how LiftApp should prescribe work: how many reps, at what load, how close
to failure, how much of it, and how all of that changes from set to set, week to week, and block to
block.

It is written to be implemented, so it commits to numbers. Where the evidence does not support a
number, it says so rather than inventing one — the sections marked **Weak** are the ones where the
app should stay out of the user's way.

## What the app actually has to decide

A prescription for one exercise in one workout is five numbers, and today [`Goal`][goal] only
carries two and a half of them:

| Decision | In `Goal` today |
| --- | --- |
| How many sets | `sets` |
| What rep target | `minReps`..`maxReps` |
| How close to failure (RIR) | **missing** |
| What load | **missing** |
| How long to rest | `restTime` |

`Goal` is also flat: one prescription for every set of the exercise. That is the single biggest
modelling gap, because the interesting parts of programming — fatigue within a session, a top set
followed by back-offs, a load that rises while reps hold — are all *differences between sets*.

Everything below feeds two outputs the app should be able to compute:

1. **A target for the set the user is about to do**, derived from what they did last time.
2. **A weekly volume picture per muscle**, derived from the active [`Plan`][plan].

## Part 1 — What actually separates strength from hypertrophy

Three variables matter, and they matter differently for the two goals.

| | Strength | Hypertrophy |
| --- | --- | --- |
| **Load** | Decisive. Needs to be heavy. | Largely irrelevant across a wide band. |
| **Proximity to failure** | Secondary. Failure is not required and is usually a bad trade. | Decisive. The set has to be hard. |
| **Volume** | Plateaus early. | Keeps paying, with diminishing returns. |

That table is the whole document in miniature. Strength is a *load* problem; hypertrophy is an
*effort × volume* problem.

### Load and rep range

The old "1–5 strength / 6–12 size / 15+ endurance" continuum is wrong about the middle band.
Schoenfeld and Grgic's re-examination of the repetition continuum found similar whole-muscle growth
across loads from roughly 30% 1RM upward — 2–4 reps, 8–12 reps and 25–35 reps produced equivalent
gains in cross-sectional area when volume was equated ([Schoenfeld & Grgic 2021][repcont]). The 2026
ACSM position stand, an overview of 137 systematic reviews, reaches the same conclusion: hypertrophy
was not meaningfully changed by load from low to high provided effort was sufficient
([ACSM 2026][acsm]).

The load-independence has one hard condition attached: **it only holds when the set is taken to or
near failure.** The studies pooled in those reviews trained to failure, and mechanistically that is
the point — at 30% 1RM the early reps recruit almost nothing, so the stimulus lives entirely in the
last few reps. A light set stopped early is a wasted set in a way a heavy set stopped early is not.

Strength does not share that freedom. Load is the adaptation, so ≥80% 1RM is the recommendation for
maximal strength, and ACSM's strength prescription is ~80% 1RM for 2–3 sets per exercise
([ACSM 2026][acsm]).

The practical consequence for defaults is that rep range should be chosen for **fatigue economy and
measurability**, not because a range is magic:

- Heavy compounds in high rep ranges are brutally fatiguing and technically degrading. A set of 20
  squats costs far more recovery than a set of 20 leg extensions and grows the quads no better.
- Isolation work in very low rep ranges is a joint-loading problem with no upside — there is no
  strength goal on a cable lateral raise, and the load is too small for the neural adaptation to
  transfer anywhere. Keep isolation at ~5 reps and above.
- Compounds should carry most of the load-driven work; a common split is 60–70% of working sets on
  compounds, 30–40% on isolation.

### Proximity to failure (RIR)

RIR is the number of reps the user could still have done. RPE on the 1–10 scale is its inverse:
`RPE = 10 - RIR` ([Ripped Body][rpe], [MASS][mass]).

The productive band is **0–4 RIR**. Further from failure than that and the hypertrophy stimulus
falls off sharply; past failure and fatigue rises faster than stimulus. Within that band, recent
trials in trained lifters found 1–3 RIR matched training to momentary failure for hypertrophy
([Refalo et al. 2024][refalo], [Pelland/Refalo near-failure work][nearfail]).

For strength the picture inverts: proximity to failure is not what drives 1RM, and taking heavy
compounds to failure buys fatigue that costs the next session. Heavy work belongs at 1–3 RIR.

**RIR is a noisy measurement and the app must treat it as such.** Lifters underestimate by about one
rep on average. Accuracy is roughly ±1 rep when true RIR is 0–5 and degrades past ±2 reps when true
RIR is 7–10; it is better at heavy loads and low reps than at light loads and high reps
([Stronger by Science][rirsbs], Zourdos et al. found real error even at a claimed 1 RIR). Three
implementation rules follow:

1. Never ask the user for an RIR above 4 — they cannot tell, and it does not change the prescription.
2. Collect it as a small integer set (`0, 1, 2, 3, 4+`), not a decimal.
3. Weight it less when deriving load for high-rep isolation than for low-rep compounds.

### Volume

Weekly sets per muscle is the dose. The landmark vocabulary is worth adopting because it maps onto
distinct app behaviours ([RP Strength][rpvol]):

| Landmark | Value | What the app does with it |
| --- | --- | --- |
| **MV** — maintenance | ~6 sets/muscle/week | The deload target, and the floor for a muscle the user is not prioritising. |
| **MEV** — minimum effective | ~8–10 sets for most trained lifters | Where a block starts. |
| **MAV** — maximum adaptive | ~10–20 sets | The band a block progresses through. |
| **MRV** — maximum recoverable | Individual; ~20+ | Where a block ends and a deload is due. |

The 2024/2025 meta-regressions by Pelland et al. are the best current dose-response data, and they
split the two goals cleanly: **strength** shows strong diminishing returns and a functional plateau
at a low weekly volume, while **hypertrophy** keeps improving with volume across the studied range
with no clear plateau ([Pelland et al., *Sports Medicine*][pelland]). ACSM's simpler summary is ~10
sets per muscle per week for hypertrophy ([ACSM 2026][acsm]).

Two caveats to encode:

- **Per session there is a soft ceiling.** The session-level dose-response is logarithmic; most of
  the available effect is captured by roughly 6–8 hard sets per muscle in one session, and ~20 sets
  per muscle per week is where returns become sharply diminishing for trained lifters. There is no
  cliff, so the app should surface this as guidance, not as a validation error. **This is the most
  contested number in the field** — do not build anything that breaks if it moves.
- **Indirect volume counts.** Pelland et al. classify every set as direct or indirect depending on
  its specificity to the measured muscle. The app already has the data for this in
  [`MuscleContainer`][mc] and should use it — see [Volume accounting](#volume-accounting).

### Frequency and rest

**Frequency**: at least 2×/week per muscle ([ACSM 2026][acsm]). For hypertrophy, frequency is a
tool for *distributing* volume under the per-session ceiling, not an independent driver — there is
strong evidence it does not meaningfully affect hypertrophy when weekly volume is equated
([Pelland et al.][pelland]). For strength it does matter independently, because heavy lifting is a
skill that benefits from practice.

**Rest**: 1–2 minutes is the floor for hypertrophy, and under 60 seconds measurably costs both
growth and reps ([Stronger by Science][rest], [Singer et al. 2024][restmeta]). Longer rest gives
small further gains in both hypertrophy and strength at a large cost in session length. Sensible
defaults:

| | Rest |
| --- | --- |
| Heavy compound (≤6 reps) | 3 min |
| Moderate compound | 2 min |
| Isolation | 90 s |
| Superset (between rounds) | 2 min |

Rest also changes the rep drop-off between sets: with 1 minute of rest reps fall from the second set
onward, with 3–5 minutes not until the third ([Singer et al. 2024][restmeta]). That interaction
belongs in the per-set fatigue model below.

### What does not matter — do not build it

The 2026 ACSM position stand explicitly found that **complex periodization did not consistently
affect outcomes** for healthy adults, and meta-analyses comparing linear against daily undulating
periodization find no meaningful difference for either strength or hypertrophy
([ACSM 2026][acsm], [Stronger by Science][per]). Equipment modality and training-to-failure likewise
did not consistently change outcomes.

So: **do not build a periodization engine.** Build progressive overload plus fatigue management, and
let users who want undulation get it by writing different routines into a [`Plan`][plan] — which the
model already supports.

Similarly, the **"effective reps" / "stimulating reps"** model — the claim that only the last ~5 reps
before failure count — is a plausible mechanism with little direct experimental support and active
methodological criticism. **Weak.** Do not make it a metric in the UI; RIR already captures the part
of it that is defensible.

## Part 2 — Defaults

Defaults have to come from somewhere the app already knows. Two axes are available:
[`ExerciseType`][type], and whether the exercise is compound or isolation — which the model does
*not* record today (see [gaps](#part-5--what-this-app-is-missing)).

### Rep range and RIR by exercise role

| Role | Strength intent | Hypertrophy intent | Both |
| --- | --- | --- | --- |
| **Primary compound** (squat, bench, deadlift, overhead press, weighted pull-up) | 3–5 reps, 1–2 RIR | 6–10 reps, 1–2 RIR | 4–6 reps, 1–2 RIR |
| **Secondary compound** (row, leg press, dip, lunge, RDL) | 5–8 reps, 1–2 RIR | 8–12 reps, 0–2 RIR | 6–10 reps, 1–2 RIR |
| **Isolation** (curl, extension, raise, calf, fly) | — | 10–15 reps, 0–1 RIR | 10–15 reps, 0–1 RIR |
| **Bodyweight-limited** (`Calisthenics`, `Reps`) | Add load, then 5–8 | 8–20 reps, 0–1 RIR | 8–15 reps, 0–1 RIR |

The pattern to notice: **RIR gets tighter as load gets lighter.** That is not arbitrary — it is the
condition attached to load-independence. A heavy triple at 2 RIR is already a near-maximal
recruitment event; a set of 15 lateral raises at 2 RIR is not.

Deadlifts deserve a note: the fatigue cost per set is the highest of any common lift, so they belong
at the low-rep, low-set end regardless of intent. This is exercise-specific knowledge the app cannot
derive from muscle lists, and is an argument for a per-exercise fatigue-cost hint.

### Where RIR does not apply

`Cardio` sets have no meaningful RIR, and `Time` sets have a degenerate one. Programming in the sense
of this document covers `Weight`, `Calisthenics` and `Reps`. Any RIR field must be nullable and any
progression policy must be skippable for the other two types.

## Part 3 — Progression on three timescales

These are three different mechanisms and the app should implement them as three different things.
Doing all three at once in the same week is the classic way to stall.

### Within a workout: set-to-set

At a fixed load and a fixed target, reps fall. The best estimate comes from Nuzzo's meta-analysis of
29 studies of ≥4 sets to failure at a fixed load ([Stronger by Science][repsets]):

| Set | Reps, as % of set 1 |
| --- | --- |
| 1 | 100% |
| 2 | ~70% |
| 3 | ~55% |
| 4 | ~50% |
| 5+ | ~45%, then flat |

Those are sets to failure with limited rest, so they are an upper bound on drop-off, not a
prescription. The point for the app is structural: **a flat prescription of `4 × 8 @ 2 RIR` at one
load is not internally consistent.** Either the reps fall, or the load falls, or the RIR falls. Pick
one and be explicit — the three choices are established practice ([RTS fatigue percents][rts]):

| Pattern | Load | Reps | Effect | Use for |
| --- | --- | --- | --- | --- |
| **Straight sets** | fixed | fixed | RIR drifts down across sets | Simple, fine for moderate RIR starts |
| **Load drop** (back-offs) | falls ~5%/set | fixed | RIR held | More total volume; hypertrophy |
| **Rep drop** | fixed | falls | RIR held | Fewer, harder sets; strength |

The honest default is **straight sets with a declared starting RIR**, plus a note in the UI that the
last set will be closer to failure than the first. That matches what users actually do and does not
pretend to more precision than the data supports.

### Week to week: overload

**Double progression** is the right default: fill the rep range at the current load, then add load
and drop back to the bottom of the range ([Ripped Body][prog]).

```
Goal: 8–12 reps, 3 sets, 2 RIR
  W1  60 kg × 8, 8, 8
  W2  60 kg × 10, 9, 9
  W3  60 kg × 12, 11, 11   ← top of range on set 1
  W4  65 kg × 8, 8, 7      ← load up, reps reset
```

Increment sizes and cadence depend on the lifter and the equipment:

| | Load increment | Cadence |
| --- | --- | --- |
| Beginner, barbell compound | 2.5–5 kg | Every session |
| Intermediate, barbell compound | 2.5 kg | Every 2–4 weeks |
| Advanced | 1.25 kg or reps only | Multiple mesocycles |
| Dumbbells | Whatever the rack forces, often 2 kg | Every 2–3 weeks |
| Machines / cables | One pin | Every 1–3 weeks |

The equipment row matters more than it looks. A 2 kg dumbbell jump on a 10 kg lateral raise is a 20%
load increase — a full rep range's worth. **On small-load exercises the app must progress reps, not
load**, and should refuse to suggest a load increment that exceeds ~10% of current load.

Alternatively, progress volume: add 1–3 sets per week from MEV toward MRV, gated on recovery — 2–3
sets if recovery was good, 1 if moderate, none if the user is struggling ([RP Strength][rpvol]).

**Progress load *or* volume in a given week, not both.**

### Block to block: the mesocycle

A block is 4–6 weeks of accumulation followed by a deload. Two things ramp across it:

**RIR ramps down.** Start at 3–4 RIR and finish at 0–1, dropping about 1 RIR every 1–2 weeks
([RP Strength][rpprog]). This is fatigue management: early weeks buy volume cheaply, the last week
extracts maximum stimulus right before the reset.

```
W1  3 RIR    W2  2 RIR    W3  1 RIR    W4  0–1 RIR    W5  deload
```

**Volume ramps up**, MEV → MRV, evenly spaced. RP's worked example: 10 → 13 → 16 → 20 sets over four
weeks, then a deload at ~6.

**The deload.** Surveyed strength and physique athletes deload for 6.4 ± 1.7 days every 5.6 ± 2.3
weeks ([Bell et al. 2024][deloadsurvey]), which rounds to *about a week every 4–6*. The composition
that matters:

- **Cut sets by roughly half.** Volume reduction does most of the work.
- **Keep frequency.** Same days, less on each.
- **Reduce load moderately and raise RIR** (e.g. +2–3 RIR) rather than stopping.
- **Do not stop entirely.** Reduced training preserves adaptation better than cessation
  ([Coleman et al.][deloadpract]).

Note that [`Plan`][plan] has no week dimension — it is a repeating cycle of routines and rest days.
**Mesocycle-level progression is not expressible in the current model** and needs a cycle/week index
to hang off. This is the second big gap after per-set targets.

## Part 4 — The math

### Estimated 1RM

Everything load-related keys off an e1RM, and the app can compute one from any near-failure logged
set. [`OneRepMaxCalculator`][orm] currently uses Epley:

```
1RM = w × (1 + r / 30)
```

Epley has a known defect that matters here: at `r = 1` it returns 96.8% of the load, not 100%. It
also applies one fixed conversion factor to every exercise and every load, which is where the error
concentrates — the rep-to-1RM relationship is genuinely different for a 20 kg lateral raise and a
200 kg deadlift.

A recent large-scale derivation from 303,494 near-failure sets across 388 exercises lets the
conversion factor vary with the load lifted ([arXiv 2603.17495][arxiv]):

```
1RM = w × (1 + (r - 1)^0.85 / (-2.55 + 4.58 × ln(w)))
```

It reduced internal inconsistency by 17–22% against four classical formulas, improved on every one
of the 183 exercises with enough data, and is exact at `r = 1` by construction. Worth adopting —
with **one blocking caveat**: the `ln(w)` term makes the formula **unit-dependent**. The paper does
not state the unit in its abstract, and the app supports both [`MassUnit`][mass_unit] values.
Determine the source unit and normalise before applying, or the formula will be systematically wrong
for every lb user. Verify against the paper before shipping.

### RIR-adjusted e1RM

This is the piece that makes autoregulation possible, and it is one line:

```kotlin
fun estimatedOneRepMax(weight: Double, reps: Int, rir: Int): Double =
    oneRepMax(weight, reps + rir)
```

A set of 8 at 2 RIR is a 10-rep max. Without a recorded RIR this cannot be computed, which is why the
missing field on [`ExerciseSet`][set] blocks the whole feature.

### Load for a target

Invert the same curve to get a working load for a rep target and RIR. Expressed as a percentage of
e1RM (computed from the weight-dependent formula at a 100 kg reference load):

| Reps | 0 RIR | 1 RIR | 2 RIR | 3 RIR |
| --- | --- | --- | --- | --- |
| 1 | 100% | 95% | 91% | 88% |
| 3 | 91% | 88% | 85% | 82% |
| 5 | 85% | 82% | 79% | 77% |
| 6 | 82% | 79% | 77% | 75% |
| 8 | 77% | 75% | 73% | 71% |
| 10 | 73% | 71% | 69% | 67% |
| 12 | 69% | 67% | 65% | 64% |
| 15 | 64% | 62% | 61% | 59% |
| 20 | 57% | 55% | 54% | 53% |

Read the RIR columns as a shift along the rep axis: 8 reps at 1 RIR is the 9-rep-max load. Because
the formula is weight-dependent these percentages move — the same cells sit ~2–3 points lower at
60 kg and ~2–3 points higher at 200 kg — which is exactly the effect the classical formulas miss.

Two derived rules the UI can use directly:

- **One rep ≈ 3% of load** in the 5–12 rep band, ~4% in the 2–4 band, ~5% at 1 rep. So a user who
  missed their rep target by 2 reps should drop ~6%, not "a plate".
- **Round to the equipment.** Compute a target, then snap it to what the user can actually load. A
  suggestion of 62.4 kg is worse than 62.5 kg even though it is more precise.

### Volume accounting

[`Exercise`][ex] already carries `primaryMuscles`, `secondaryMuscles` and `tertiaryMuscles`. Weekly
volume per muscle is a fold over the active [`Plan`][plan]'s routines:

```kotlin
// Fractional weights are a convention, not a measured quantity.
// Counting only primary muscles badly underestimates biceps, triceps, and rear delts.
private const val DIRECT = 1.0
private const val INDIRECT = 0.5
private const val MINOR = 0.25
```

Sum `sets × weight` per muscle across all routines in the cycle, normalise to a week by the cycle
length, and compare against the landmarks in Part 1. Present it as a band, not a number — the input
precision does not justify one decimal place.

One modelling problem to be aware of: [`Muscle`][muscle] has a single `Shoulders` entry. Front delts
receive heavy indirect volume from every press while side and rear delts receive almost none, so
aggregate shoulder volume is the least trustworthy row in that table. Splitting the enum is a
migration; until then, do not let the app conclude the user's shoulders are covered.

## Part 5 — What this app is missing

In rough dependency order.

1. **RIR on [`ExerciseSet`][set].** Nullable `Int`, valid `0..4`, meaningless for `Cardio`/`Time`.
   Nothing autoregulates without it. The backup format is schema-driven and derives its header from
   the table, so a new column travels for free — but it is still a Room migration.

2. **Per-set targets instead of a flat [`Goal`][goal].** The current shape cannot express a top set
   plus back-offs, a load drop, or a rep drop. Either give `Goal` a list of per-set targets or add a
   set-pattern enum that generates them.

3. **A target load or `%e1RM` on `Goal`.** Currently the app prescribes reps and sets but says nothing
   about load, so the user carries the hardest decision themselves.

4. **Training intent.** Strength / hypertrophy / both, per routine or per exercise. Every default
   table in Part 2 is indexed by it.

5. **Compound vs isolation classification.** Not in the model. `primaryMuscles.size` is a tempting
   proxy and a bad one — a barbell curl has one primary muscle and a cable fly has one too. Make it
   an explicit field on [`Exercise`][ex], defaulted per seeded exercise.

6. **A week/cycle index on [`Plan`][plan].** Without it there is no mesocycle, so no RIR ramp, no
   volume ramp, and no scheduled deload.

7. **Per-exercise fatigue cost.** A three-level hint (low/moderate/high) would let the app stop
   suggesting 5 sets of 15 deadlifts. Cannot be derived from muscle lists.

8. **A progression policy per exercise.** Double progression, fixed load increment, or reps-only.
   Small-load exercises must default to reps-only.

### The autoregulation loop, once those exist

```
on completing a set:
    record weight, reps, rir
    e1RM ← oneRepMax(weight, reps + rir)

when suggesting the next session's first set:
    best ← highest e1RM for this exercise in the last N sessions
    if last session hit maxReps at ≥ target RIR on set 1:
        load ← loadFor(best, minReps, targetRir)          // double progression: load up
    else if last session missed minReps or came in under target RIR:
        load ← lastLoad × (1 - 0.03 × repsShort)          // back off ~3% per missing rep
    else:
        load ← lastLoad                                    // hold, chase reps
    load ← snapToEquipment(load, exercise)
```

Guard it with three rules:

- **Never suggest a jump over ~10% of current load**, whatever the arithmetic says.
- **Require two data points before acting.** One bad session is noise.
- **Show the reason.** "Same weight — you got 9 of 12 last time" is a coaching cue. A number that
  changes silently is a bug report.

### Stalls

Two sessions without progress on reps or load at the target RIR is a stall. Escalate in this order,
which is cheapest-first: check the RIR was honest → deload the exercise ~10% and rebuild → reduce
volume elsewhere for that muscle → change the exercise. Do not suggest changing the exercise first;
users read it as the app giving up.

## Rules of thumb worth encoding

- Effort is the non-negotiable input for hypertrophy. Load is not.
- Load is the non-negotiable input for strength. Effort is not.
- One variable progresses at a time.
- RIR is a ±1 rep measurement. Never display it as if it were exact.
- Volume ceilings are soft. Warn, never block.
- The best program is the one the user follows. Complexity that does not change the prescription is
  cost without benefit — which is also the 2026 ACSM position stand's headline finding.

[goal]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/goal/Goal.kt
[plan]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/plan/Plan.kt
[set]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/workout/ExerciseSet.kt
[orm]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exerciseset/OneRepMaxCalculator.kt
[ex]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exercise/Exercise.kt
[type]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/exercise/ExerciseType.kt
[mc]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/muscle/MuscleContainer.kt
[muscle]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/muscle/Muscle.kt
[mass_unit]: ../domain/src/main/kotlin/com/patrykandpatrick/liftapp/domain/unit/MassUnit.kt

[acsm]: https://acsm.org/resistance-training-guidelines-update-2026/
[repcont]: https://www.mdpi.com/2075-4663/9/2/32
[pelland]: https://link.springer.com/article/10.1007/s40279-025-02344-w
[refalo]: https://www.tandfonline.com/doi/full/10.1080/02640414.2024.2321021
[nearfail]: https://pmc.ncbi.nlm.nih.gov/articles/PMC10161210/
[rpvol]: https://rpstrength.com/blogs/articles/training-volume-landmarks-muscle-growth
[rpprog]: https://rpstrength.com/blogs/articles/progressing-for-hypertrophy
[rpe]: https://rippedbody.com/rpe/
[mass]: https://massresearchreview.com/2023/05/22/rpe-and-rir-the-complete-guide/
[rirsbs]: https://www.strongerbyscience.com/reps-in-reserve/
[repsets]: https://www.strongerbyscience.com/reps-sets/
[rest]: https://www.strongerbyscience.com/rest-times-for-muscle-growth/
[restmeta]: https://www.frontiersin.org/journals/sports-and-active-living/articles/10.3389/fspor.2024.1429789/full
[per]: https://www.strongerbyscience.com/periodization-data/
[prog]: https://rippedbody.com/progression/
[rts]: https://store.reactivetrainingsystems.com/blogs/advanced-concepts/fatigue-percents-revisited
[deloadsurvey]: https://link.springer.com/article/10.1186/s40798-024-00691-y
[deloadpract]: https://doras.dcu.ie/31501/1/a_practical_approach_to_deloading__recommendations.203%282%29.pdf
[arxiv]: https://arxiv.org/abs/2603.17495
