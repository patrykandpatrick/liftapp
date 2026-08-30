package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource

/**
 * The built-in exercise catalog, in insertion order. On a fresh database the rows receive the
 * autoincrement IDs 1 through 101, which match the IDs the published app gave the same exercises,
 * and the legacy migration relies on that correspondence. **This list is append-only** — never
 * reorder an entry or insert one into the middle.
 *
 * `Migration11To12.seedDefaults` and `Migration13To14` both read this list, so editing an entry
 * changes what those migrations write. `Migration13To14` replays the roles, policies and increments
 * onto an upgrading device, which is why they belong here rather than in the migration.
 *
 * The muscle tiers are volume credit, not display prominence: `MuscleVolume` gives a main muscle a
 * whole working set, a secondary half, a tertiary a quarter. Name a muscle main only where the
 * exercise trains it rather than merely working it — the triceps in a bench press are secondary.
 *
 * The classification is hand-authored, not derived. `role` is null exactly for the eleven `Cardio`
 * and `Time` entries, which have none. `progressionPolicy` is `RepsOnly` wherever a load suggestion
 * would be dishonest: everything that carries no external load, and the small-load isolation work
 * where the smallest available jump is more than a tenth of the working load. `loadIncrement` is
 * kilograms of the equipment's smallest step — 2.5 for a barbell, 2 for a dumbbell rack, 4 for
 * kettlebells, 5 for a machine pin — and null wherever there is nothing to load.
 */
@Suppress("LargeClass")
object DefaultExercises {

    val exercises: List<Exercise.Insert> = buildList {
        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.WarmUp),
                exerciseType = ExerciseType.Cardio,
                mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
                secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PullUps),
                exerciseType = ExerciseType.Calisthenics,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps, Muscle.RearDelts, Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Dips),
                exerciseType = ExerciseType.Calisthenics,
                mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DbBicepCurl),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = listOf(Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.FrontDelts),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BarbellBicepCurl),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = listOf(Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.FrontDelts),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FlatBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ChestExtension),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Deadlift),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
                secondaryMuscles =
                    listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LSit),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
                tertiaryMuscles = listOf(Muscle.FrontDelts, Muscle.Biceps),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Wipers),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
                tertiaryMuscles = listOf(Muscle.RearDelts, Muscle.Biceps),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LegRaise),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
                tertiaryMuscles = listOf(Muscle.RearDelts, Muscle.Biceps),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LongRun),
                exerciseType = ExerciseType.Cardio,
                mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
                secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Squats),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
                secondaryMuscles = listOf(Muscle.LowerBack, Muscle.Adductors),
                tertiaryMuscles = listOf(Muscle.Calves),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LegExtension),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LegCurl),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Hamstrings),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.InclineBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DeclineBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FlatDbBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.InclineDbBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DeclineDbBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DumbbellFrenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.FrontDelts),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BarbellFrenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.FrontDelts),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CableAbCurl),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Abs),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CableHipAbduction),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Abductors),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CableHipAdduction),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Adductors),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HipThrust),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Glutes),
                secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CalfRaise),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Calves),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Plank),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Quadriceps),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.AustralianPullUps),
                exerciseType = ExerciseType.Calisthenics,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps, Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.LowerBack),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ChinUps),
                exerciseType = ExerciseType.Calisthenics,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps, Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.LowerBack),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BackExtension),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.LowerBack),
                secondaryMuscles = listOf(Muscle.Lats, Muscle.Glutes),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DeclinePushUps),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest, Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DiamondPushUps),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BarbellRows),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.RearDelts),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DumbbellRows),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.RearDelts),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.StandingDumbbellOverheadPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SeatedDumbbellOverheadPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.OHP),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ExplosiveKneeRaise),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.RearDelts, Muscle.Lats),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Biceps),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FacePull),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.RearDelts),
                secondaryMuscles = listOf(Muscle.Traps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HammerCurls),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = listOf(Muscle.Forearms),
                tertiaryMuscles = listOf(Muscle.FrontDelts),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HorizontalBarDips),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PullUpHold),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps, Muscle.Traps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SeatedRows),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Traps, Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.RearDelts, Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SideArmRaise),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.SideDelts),
                secondaryMuscles = listOf(Muscle.Traps),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.TricepBarbellPushdown),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.TricepCablePushdown),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.AxeHold),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.SideDelts),
                secondaryMuscles = listOf(Muscle.Traps),
                tertiaryMuscles = emptyList(),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BenchPressNarrowGrip),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                secondaryMuscles = listOf(Muscle.Chest, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.Glutes),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HollowBody),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.FrontDelts),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DumbbellConcentrationCurl),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PreacherCurls),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SeatedTricepPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Triceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ZCurls),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Biceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BarbellLunges),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.BulgarianSplitSquat),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FrontSquat),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.GluteBridge),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Glutes),
                secondaryMuscles = listOf(Muscle.Hamstrings),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.GoodMornings),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Hamstrings),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HighKneeJumps),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Hamstrings, Muscle.Quadriceps, Muscle.Calves),
                secondaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.KettlebellSwing),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Glutes),
                secondaryMuscles = listOf(Muscle.Hamstrings),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 4.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LegPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.OverheadSquat),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.SideDelts, Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PistolSquat),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.RomanianDeadlift),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
                secondaryMuscles =
                    listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.StationaryBike),
                exerciseType = ExerciseType.Cardio,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes, Muscle.Hamstrings),
                tertiaryMuscles = listOf(Muscle.Calves),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CableWoodChoppers),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = listOf(Muscle.SideDelts, Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CrunchesWithLegsUp),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FlutterKicks),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.NegativeCrunches),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SideCrunch),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SidePlank),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.TurkishGetUp),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Abs),
                secondaryMuscles = listOf(Muscle.SideDelts, Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Burpees),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Glutes, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Abs),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Butterfly),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CableCrossOver),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FlyWithDumbbells),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.FlyWithCable),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SideToSidePushUps),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Triceps),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HyperExtension),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.LowerBack),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LatPulldown),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.LowRow),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PendelayRow),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.RackDeadlift),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
                secondaryMuscles =
                    listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
                tertiaryMuscles = listOf(Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.TBarRow),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ShotgunRow),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.StraightArmPulldown),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ArnoldShoulderPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Traps),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ReverseButterfly),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.RearDelts),
                secondaryMuscles = listOf(Muscle.Traps),
                tertiaryMuscles = emptyList(),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Shrugs),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Traps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.Snatch),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.SideDelts),
                secondaryMuscles = listOf(Muscle.LowerBack),
                tertiaryMuscles =
                    listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.WallHandstand),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.SideDelts),
                secondaryMuscles = listOf(Muscle.LowerBack),
                tertiaryMuscles =
                    listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.CalfPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Calves),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 5.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.MuscleUps),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Lats, Muscle.Chest),
                secondaryMuscles =
                    listOf(Muscle.FrontDelts, Muscle.Biceps, Muscle.Traps, Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.SuperMan),
                exerciseType = ExerciseType.Time,
                mainMuscles = listOf(Muscle.LowerBack),
                secondaryMuscles = listOf(Muscle.RearDelts, Muscle.Hamstrings),
                role = null,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.HighPull),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.SideDelts),
                secondaryMuscles = listOf(Muscle.Glutes),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.RearDeltRaise),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.RearDelts),
                secondaryMuscles = listOf(Muscle.Traps),
                role = ExerciseRole.Isolation,
                progressionPolicy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.0,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.PowerClean),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles =
                    listOf(Muscle.Chest, Muscle.Triceps, Muscle.Quadriceps, Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Forearms),
                role = ExerciseRole.PrimaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.IsometricWipers),
                exerciseType = ExerciseType.Reps,
                mainMuscles = listOf(Muscle.Chest, Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.RepsOnly,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.ReverseGripBenchPress),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
            )
            .also(::add)

        Exercise.Insert(
                name = Name.Resource(ExerciseStringResource.DumbbellLunges),
                exerciseType = ExerciseType.Weight,
                mainMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
                role = ExerciseRole.SecondaryCompound,
                progressionPolicy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.0,
            )
            .also(::add)
    }
}
