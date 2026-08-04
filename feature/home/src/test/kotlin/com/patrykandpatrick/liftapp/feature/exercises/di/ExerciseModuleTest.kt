package com.patrykandpatrick.liftapp.feature.exercises.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.navigation.Routes
import kotlin.test.assertSame
import org.junit.Test

class ExerciseModuleTest {
    @Test
    fun `argument-free exercises tab uses view mode route data`() {
        val routeData = ExerciseModule.provideExerciseListRouteData(SavedStateHandle())

        assertSame(Routes.Home.Exercises, routeData)
    }
}
