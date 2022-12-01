package com.patrykandpatryk.liftapp.feature.newroutine.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Event
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Intent
import com.patrykandpatryk.liftapp.feature.newroutine.domain.NewRoutineScreenStateHandler
import com.patrykandpatryk.liftapp.feature.newroutine.domain.ScreenState
import com.patrykandpatryk.liftapp.feature.newroutine.mapper.ExerciseToItemMapper
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewRoutineModule {

    @Binds
    fun bindScreenStateHandler(handler: NewRoutineScreenStateHandler): ScreenStateHandler<ScreenState, Intent, Event>

    @Binds
    fun bindMapper(mapper: ExerciseToItemMapper): Mapper<Exercise, ExerciseItem>

    companion object {

        @Provides
        @RoutineId
        fun provideRoutineId(savedStateHandle: SavedStateHandle): Long? =
            savedStateHandle.get<Long>(Routes.ARG_ID)?.takeIf { it > 0 }
    }
}
