package com.patrykandpatrick.liftapp.functionality.database.exercise

import android.content.Context
import androidx.annotation.StringRes
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.functionality.database.R
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class InsertDefaultRoutines
@Inject
constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: ExerciseDao,
    @param:ApplicationContext private val context: Context,
) {

    suspend operator fun invoke() {
        val exerciseIDsByResource =
            exerciseDao
                .getAllExercises()
                .first()
                .mapNotNull { exercise ->
                    val resource =
                        (exercise.name as? Name.Resource)?.resource as? ExerciseStringResource
                    resource?.let { it to exercise.id }
                }
                .toMap()

        DefaultRoutines.forEach { routine ->
            val routineID =
                routineDao.upsert(RoutineEntity(name = context.getString(routine.nameResource)))
            routineDao.replaceItems(
                routineID,
                routine.exercises.map { exerciseResource ->
                    RoutineItem.exercise(
                        checkNotNull(exerciseIDsByResource[exerciseResource]) {
                            "Default exercise $exerciseResource was not inserted"
                        }
                    )
                },
            )
        }
    }
}

private data class DefaultRoutine(
    @param:StringRes val nameResource: Int,
    val exercises: List<ExerciseStringResource>,
)

private val DefaultRoutines =
    listOf(
        DefaultRoutine(
            nameResource = R.string.routine_full_body_1,
            exercises =
                listOf(
                    ExerciseStringResource.FlatBenchPress,
                    ExerciseStringResource.OHP,
                    ExerciseStringResource.BarbellRows,
                    ExerciseStringResource.Deadlift,
                    ExerciseStringResource.Squats,
                    ExerciseStringResource.DbBicepCurl,
                    ExerciseStringResource.LegRaise,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_full_body_2,
            exercises =
                listOf(
                    ExerciseStringResource.InclineBenchPress,
                    ExerciseStringResource.SeatedDumbbellOverheadPress,
                    ExerciseStringResource.DumbbellRows,
                    ExerciseStringResource.RomanianDeadlift,
                    ExerciseStringResource.BarbellLunges,
                    ExerciseStringResource.BarbellBicepCurl,
                    ExerciseStringResource.ExplosiveKneeRaise,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_full_body_3,
            exercises =
                listOf(
                    ExerciseStringResource.InclineDbBenchPress,
                    ExerciseStringResource.FlyWithDumbbells,
                    ExerciseStringResource.SideArmRaise,
                    ExerciseStringResource.ChinUps,
                    ExerciseStringResource.LegCurl,
                    ExerciseStringResource.BulgarianSplitSquat,
                    ExerciseStringResource.CableAbCurl,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_full_body_4,
            exercises =
                listOf(
                    ExerciseStringResource.DeclineDbBenchPress,
                    ExerciseStringResource.Dips,
                    ExerciseStringResource.RearDeltRaise,
                    ExerciseStringResource.AustralianPullUps,
                    ExerciseStringResource.Deadlift,
                    ExerciseStringResource.Squats,
                    ExerciseStringResource.FlutterKicks,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_push,
            exercises =
                listOf(
                    ExerciseStringResource.FlatBenchPress,
                    ExerciseStringResource.InclineBenchPress,
                    ExerciseStringResource.FlyWithDumbbells,
                    ExerciseStringResource.OHP,
                    ExerciseStringResource.BarbellFrenchPress,
                    ExerciseStringResource.Squats,
                    ExerciseStringResource.DumbbellLunges,
                    ExerciseStringResource.LegRaise,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_pull,
            exercises =
                listOf(
                    ExerciseStringResource.ChinUps,
                    ExerciseStringResource.BarbellRows,
                    ExerciseStringResource.DumbbellRows,
                    ExerciseStringResource.OHP,
                    ExerciseStringResource.BarbellBicepCurl,
                    ExerciseStringResource.Deadlift,
                    ExerciseStringResource.LegCurl,
                    ExerciseStringResource.FlutterKicks,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_calisthenics_1,
            exercises =
                listOf(
                    ExerciseStringResource.ChinUps,
                    ExerciseStringResource.ExplosiveKneeRaise,
                    ExerciseStringResource.AustralianPullUps,
                    ExerciseStringResource.PullUpHold,
                    ExerciseStringResource.PullUps,
                    ExerciseStringResource.FlutterKicks,
                ),
        ),
        DefaultRoutine(
            nameResource = R.string.routine_calisthenics_2,
            exercises =
                listOf(
                    ExerciseStringResource.Dips,
                    ExerciseStringResource.ExplosiveKneeRaise,
                    ExerciseStringResource.HorizontalBarDips,
                    ExerciseStringResource.DeclinePushUps,
                    ExerciseStringResource.DiamondPushUps,
                    ExerciseStringResource.LegRaise,
                ),
        ),
    )
