package com.patrykandpatryk.liftapp.core.image

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GlideEntryPoint {
    val muscleContainerModelLoaderFactory: MuscleContainerModelLoader.Factory
}
