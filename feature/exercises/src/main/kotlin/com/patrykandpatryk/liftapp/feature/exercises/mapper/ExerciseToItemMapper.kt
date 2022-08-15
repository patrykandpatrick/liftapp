package com.patrykandpatryk.liftapp.feature.exercises.mapper

import android.app.Application
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.core.ui.resource.stringRes
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem
import javax.inject.Inject

class ExerciseToItemMapper @Inject constructor(
    private val application: Application,
) : Mapper<@JvmSuppressWildcards Pair<Exercise, String>, ExercisesItem.Exercise> {

    override suspend fun map(input: Pair<Exercise, String>): ExercisesItem.Exercise {

        val (exercise, key) = input

        return ExercisesItem.Exercise(
            id = exercise.id,
            name = exercise.displayName,
            key = key,
            muscles = exercise
                .mainMuscles
                .joinToPrettyString(
                    andText = application.getString(R.string.and_in_a_list),
                ) { muscle -> application.getString(muscle.stringRes) },
            iconRes = exercise.exerciseType.iconRes,
        )
    }
}
