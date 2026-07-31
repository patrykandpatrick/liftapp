package com.patrykandpatrick.liftapp.core.image

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.patrykandpatrick.liftapp.domain.muscle.MuscleImageProvider
import java.io.InputStream
import javax.inject.Inject

class MuscleImageModelLoader(private val muscleImageProvider: MuscleImageProvider) :
    ModelLoader<MuscleImageModel, InputStream> {

    override fun buildLoadData(
        model: MuscleImageModel,
        width: Int,
        height: Int,
        options: Options,
    ): ModelLoader.LoadData<InputStream> =
        ModelLoader.LoadData(
            ObjectKey(muscleImageProvider.getMuscleImageName(model.muscleContainer, model.isDark)),
            MuscleContainerDataFetcher(
                model.muscleContainer,
                model.isDark,
                muscleImageProvider,
            ),
        )

    override fun handles(model: MuscleImageModel): Boolean = true

    class Factory @Inject constructor(private val muscleImageProvider: MuscleImageProvider) :
        ModelLoaderFactory<MuscleImageModel, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): MuscleImageModelLoader =
            MuscleImageModelLoader(muscleImageProvider)

        override fun teardown() = Unit
    }
}
