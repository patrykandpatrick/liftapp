package com.patrykandpatryk.liftapp.functionality.musclebitmap.provider

import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatryk.liftapp.functionality.musclebitmap.MuscleBitmapGeneratorImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MuscleImageProviderImpl @Inject constructor(
    private val muscleImageGeneratorImpl: MuscleBitmapGeneratorImpl,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : MuscleImageProvider {

    override suspend fun getMuscleImagePath(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
    ): String {

        withContext(defaultDispatcher) {
            muscleImageGeneratorImpl.generateBitmap(
                primaryMuscles = primaryMuscles,
                secondaryMuscles = secondaryMuscles,
                tertiaryMuscles = tertiaryMuscles,
            )
        }

        return "" // TODO
    }
}
