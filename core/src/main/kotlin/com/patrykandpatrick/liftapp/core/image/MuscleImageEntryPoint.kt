package com.patrykandpatrick.liftapp.core.image

import com.patrykandpatrick.liftapp.domain.muscle.MuscleImageProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MuscleImageEntryPoint {
    val muscleImageProvider: MuscleImageProvider
}
