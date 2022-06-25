package com.patrykandpatryk.liftapp.feature.exercise.mapper

import android.app.Application
import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.core.ui.resource.stringRes
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExercisesItem
import javax.inject.Inject

class ExerciseToItemMapper @Inject constructor(
    private val application: Application,
) : Mapper<Exercise, ExercisesItem.Exercise> {

    override fun map(input: Exercise): ExercisesItem.Exercise =
        ExercisesItem.Exercise(
            id = input.id,
            name = input.name,
            muscles = input
                .mainMuscles
                .joinToPrettyString(
                    andText = application.getString(R.string.and_in_a_list),
                ) { muscle -> application.getString(muscle.stringRes) },
            iconRes = input.exerciseType.iconRes,
        )
}
