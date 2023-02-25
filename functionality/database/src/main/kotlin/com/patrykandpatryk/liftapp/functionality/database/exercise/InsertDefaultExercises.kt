package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.functionality.database.string.ExerciseStringResource
import javax.inject.Inject

@Suppress("LargeClass")
class InsertDefaultExercises @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {

    private val exercises: List<Exercise.Insert> = buildList {

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.WarmUp),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PullUps),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats, Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Dips),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DbBicepCurl),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BarbellBicepCurl),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FlatBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ChestExtension),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Deadlift),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LSit),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Wipers),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LegRaise),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LongRun),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Squats),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.LowerBack, Muscle.Adductors),
            tertiaryMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LegExtension),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LegCurl),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.InclineBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DeclineBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FlatDbBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.InclineDbBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DeclineDbBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DumbbellFrenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BarbellFrenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CableAbCurl),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CableHipAbduction),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Adductors),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CableHipAdduction),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abductors),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HipThrust),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CalfRaise),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Plank),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Quadriceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.AustralianPullUps),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps, Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ChinUps),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats, Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BackExtension),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DeclinePushUps),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DiamondPushUps),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BarbellRows),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DumbbellRows),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.StandingDumbbellOverheadPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SeatedDumbbellOverheadPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.OHP),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ExplosiveKneeRaise),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders, Muscle.Lats),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FacePull),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HammerCurls),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HorizontalBarDips),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PullUpHold),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SeatedRows),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SideArmRaise),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.TricepBarbellPushdown),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.TricepCablePushdown),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.AxeHold),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BenchPressNarrowGrip),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HollowBody),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DumbbellConcentrationCurl),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PreacherCurls),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SeatedTricepPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ZCurls),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BarbellLunges),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.BulgarianSplitSquat),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FrontSquat),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.GluteBridge),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Hamstrings),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.GoodMornings),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Hamstrings),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HighKneeJumps),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Quadriceps, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.KettlebellSwing),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LegPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.OverheadSquat),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PistolSquat),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.RomanianDeadlift),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.StationaryBike),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes, Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CableWoodChoppers),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CrunchesWithLegsUp),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FlutterKicks),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.NegativeCrunches),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SideCrunch),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SidePlank),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.TurkishGetUp),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Burpees),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Glutes, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Butterfly),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CableCrossOver),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FlyWithDumbbells),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.FlyWithCable),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SideToSidePushUps),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Triceps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HyperExtension),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LatPulldown),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.LowRow),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PendelayRow),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.RackDeadlift),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.TBarRow),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ShotgunRow),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.StraightArmPulldown),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ArnoldShoulderPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Traps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ReverseButterfly),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Shrugs),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.Snatch),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.LowerBack),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.WallHandstand),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.LowerBack),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.CalfPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.MuscleUps),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps, Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.SuperMan),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.HighPull),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.RearDeltRaise),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.PowerClean),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Chest, Muscle.Triceps, Muscle.Quadriceps, Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.IsometricWipers),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.ReverseGripBenchPress),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(ExerciseStringResource.DumbbellLunges),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)
    }

    suspend operator fun invoke() {
        exerciseRepository.insert(exercises)
    }
}
