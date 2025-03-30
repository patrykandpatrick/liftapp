package com.patrykandpatryk.liftapp.core.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer
import dagger.hilt.EntryPoints
import java.io.InputStream

@GlideModule
class GlideApp : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val entryPoint = EntryPoints.get(context, GlideEntryPoint::class.java)
        registry.prepend(
            MuscleContainer::class.java,
            InputStream::class.java,
            entryPoint.muscleContainerModelLoaderFactory,
        )
    }
}
