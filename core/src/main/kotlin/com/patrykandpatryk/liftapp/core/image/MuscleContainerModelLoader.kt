package com.patrykandpatryk.liftapp.core.image

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import java.io.InputStream
import javax.inject.Inject

class MuscleContainerModelLoader(
    private val muscleImageProvider: MuscleImageProvider,
    private val isDarkModeReceiver: IsDarkModeReceiver,
) : ModelLoader<MuscleContainer, InputStream> {

    override fun buildLoadData(
        model: MuscleContainer,
        width: Int,
        height: Int,
        options: Options,
    ): ModelLoader.LoadData<InputStream>? {
        val isDark = isDarkModeReceiver().value
        return ModelLoader.LoadData(
            ObjectKey(muscleImageProvider.getMuscleImageName(model, isDark)),
            MuscleContainerDataFetcher(model, isDark, muscleImageProvider),
        )
    }

    override fun handles(model: MuscleContainer): Boolean = true

    class Factory
    @Inject
    constructor(
        private val muscleImageProvider: MuscleImageProvider,
        private val isDarkModeReceiver: IsDarkModeReceiver,
    ) : ModelLoaderFactory<MuscleContainer, InputStream> {
        override fun build(
            multiFactory: MultiModelLoaderFactory
        ): ModelLoader<MuscleContainer, InputStream> {
            return MuscleContainerModelLoader(muscleImageProvider, isDarkModeReceiver)
        }

        override fun teardown() = Unit
    }
}
