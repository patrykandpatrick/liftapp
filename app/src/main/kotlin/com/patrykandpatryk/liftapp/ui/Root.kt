package com.patrykandpatryk.liftapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.provider.ProvideNavHost
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.about.ui.addAbout
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui.addBodyMeasurementDetailDestination
import com.patrykandpatryk.liftapp.feature.exercise.ui.addExerciseDetails
import com.patrykandpatryk.liftapp.feature.exercises.ui.addExercises
import com.patrykandpatryk.liftapp.feature.main.ui.addHome
import com.patrykandpatryk.liftapp.feature.newexercise.ui.addNewExercise
import com.patrykandpatryk.liftapp.feature.newroutine.ui.addNewRoutine
import com.patrykandpatryk.liftapp.feature.onerepmax.ui.addOneRepMax
import com.patrykandpatryk.liftapp.feature.routine.ui.addRoutine
import com.patrykandpatryk.liftapp.feature.settings.ui.addSettings

@Composable
fun Root(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = darkTheme.not(),
            isNavigationBarContrastEnforced = false,
        )
    }

    LiftAppTheme(darkTheme = darkTheme) {
        ProvideNavHost(navHostController = navController) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home.value,
                modifier = modifier,
            ) {
                addHome(navController)
                addAbout()
                addSettings()
                addOneRepMax()
                addNewRoutine()
                addBodyMeasurementDetailDestination()
                addNewExercise()
                addExerciseDetails()
                addExercises()
                addRoutine()
            }
        }
    }
}
