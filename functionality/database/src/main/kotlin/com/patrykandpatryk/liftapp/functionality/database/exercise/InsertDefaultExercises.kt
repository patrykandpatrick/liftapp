package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.functionality.database.R
import javax.inject.Inject

@Suppress("LargeClass")
class InsertDefaultExercises @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
) {

    private val exercises: List<Exercise.Insert> = buildList {

        Exercise.Insert(
            name = Name.Resource(R.string::ex_warm_up.name),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_pull_ups.name),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats, Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_dips.name),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_db_bicep_curl.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_barbell_bicep_curl.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_flat_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_chest_extension.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_deadlift.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_l_sit.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_wipers.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_leg_rises.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_long_run.name),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_squats.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps, Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.LowerBack, Muscle.Adductors),
            tertiaryMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_leg_extensions.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_leg_curl.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_incline_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_decline_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_flat_db_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_incline_db_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_decline_db_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_dumbbell_french_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_barbell_french_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_cable_ab_curl.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_cable_hip_adduction.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Adductors),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_cable_hip_abduction.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abductors),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_hip_thrust.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_calf_rises.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_plank.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Quadriceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_australian_pull_ups.name),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps, Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_chin_ups.name),
            exerciseType = ExerciseType.Calisthenics,
            mainMuscles = listOf(Muscle.Lats, Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_back_extension.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_decline_push_ups.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_diamond_push_ups.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_barbell_rows.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_dumbbell_rows.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_standing_dumbbell_overhead_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_seated_dumbbell_overhead_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_ohp.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_explosive_knee_rises.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Shoulders, Muscle.Lats),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Biceps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_face_pull.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_hammer_curls.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(Muscle.Forearms),
            tertiaryMuscles = listOf(Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_horizontal_bar_dips.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_pull_up_hold.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_seated_rows.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Traps, Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_side_arm_rises.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_tricep_barbell_pushdown.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_tricep_cable_pushdown.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_axe_hold.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_bench_press_narrow_grip.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_hollow_body.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Quadriceps, Muscle.Shoulders),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_dumbbell_concentration_curl.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_preacher_curls.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_seated_tricep_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_z_curls.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Biceps),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_barbell_lunges.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_bulgarian_split_squat.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_front_squat.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_glute_bridge.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Hamstrings),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_good_mornings.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Hamstrings),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_high_knee_jumps.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Hamstrings, Muscle.Quadriceps, Muscle.Calves),
            secondaryMuscles = listOf(Muscle.Abs, Muscle.LowerBack),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_kettlebell_swing.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Glutes),
            secondaryMuscles = listOf(Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_leg_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_overhead_squat.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_pistol_squat.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_romanian_deadlift.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_stationary_bike.name),
            exerciseType = ExerciseType.Cardio,
            mainMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes, Muscle.Hamstrings),
            tertiaryMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_cable_woodchoppers.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(Muscle.Shoulders, Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_crunches_with_legs_up.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_flutter_kicks.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_negative_crunches.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_side_crunch.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_side_plank.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_turkish_get_up.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Abs),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_burpees.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Glutes, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Quadriceps, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_butterfly.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_cable_cross_over.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_fly_with_dumbbells.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_fly_with_cable.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_side_to_side_push_ups.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Triceps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_hyperextension.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_lat_pulldown.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_low_row.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_pendelay_row.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_rack_deadlift.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.LowerBack, Muscle.Glutes, Muscle.Hamstrings),
            secondaryMuscles = listOf(Muscle.Forearms, Muscle.Lats, Muscle.Quadriceps, Muscle.Traps),
            tertiaryMuscles = listOf(Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_t_bar_row.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Traps),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_shotgun_row.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_straight_arm_pulldown.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_arnold_shoulder_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Traps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_reverse_butterfly.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
            tertiaryMuscles = listOf(),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_shrugs.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_snatch.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.LowerBack),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_wall_handstand.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.LowerBack),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Hamstrings, Muscle.Glutes, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_calf_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Calves),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_muscle_ups.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Lats, Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Biceps, Muscle.Traps, Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_superman.name),
            exerciseType = ExerciseType.Time,
            mainMuscles = listOf(Muscle.LowerBack),
            secondaryMuscles = listOf(Muscle.Shoulders, Muscle.Hamstrings),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_high_pull.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_rear_delt_raise.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Traps),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_power_clean.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Chest, Muscle.Triceps, Muscle.Quadriceps, Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Forearms),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_isometric_wipers.name),
            exerciseType = ExerciseType.Reps,
            mainMuscles = listOf(Muscle.Chest, Muscle.Shoulders),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = listOf(Muscle.LowerBack, Muscle.Lats, Muscle.Glutes),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_reverse_grip_bench_press.name),
            exerciseType = ExerciseType.Weight,
            mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Shoulders),
            tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
        ).also(::add)

        Exercise.Insert(
            name = Name.Resource(R.string::ex_dumbbell_lunges.name),
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
