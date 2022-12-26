package com.patrykandpatryk.liftapp.feature.routine.mapper

import com.patrykandpatryk.liftapp.core.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.feature.routine.model.ExerciseItem
import javax.inject.Inject

class ExerciseToItemMapper @Inject constructor(
    private val stringProvider: StringProvider,
) : Mapper<Exercise, ExerciseItem> {

    override suspend fun map(input: Exercise): ExerciseItem =
        ExerciseItem(
            id = input.id,
            name = input.displayName,
            muscles = input
                .mainMuscles
                .joinToPrettyString(
                    andText = stringProvider.andInAList,
                    toString = stringProvider::getMuscleName,
                ),
            iconRes = input.exerciseType.iconRes,
        )
}
