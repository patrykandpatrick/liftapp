package com.patrykandpatryk.liftapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.exercise.ui.Exercise
import com.patrykandpatryk.liftapp.feature.main.ui.Home
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExercise
import com.patrykandpatryk.liftapp.feature.onerepmax.ui.OneRepMax
import com.patrykandpatryk.liftapp.feature.settings.ui.Settings

@Composable
@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
fun Root(modifier: Modifier = Modifier) {

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val systemUiController = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = darkTheme.not(),
            isNavigationBarContrastEnforced = false,
        )
    }

    LiftAppTheme(darkTheme = darkTheme) {

        ModalBottomSheetLayout(
            modifier = modifier,
            bottomSheetNavigator = bottomSheetNavigator,
        ) {

            AnimatedNavHost(
                navController = navController,
                startDestination = Routes.Home.value,
            ) {

                composable(route = Routes.Home.value) {
                    Home(parentNavController = navController)
                }

                composable(route = Routes.About.value) {
                    About()
                }

                composable(route = Routes.Settings.value) {
                    Settings(parentNavController = navController)
                }

                composable(route = Routes.OneRepMax.value) {
                    OneRepMax(parentNavController = navController)
                }

                composable(
                    route = Routes.NewExercise.value,
                    arguments = Routes.NewExercise.navArguments,
                ) {
                    NewExercise(popBackStack = { navController.popBackStack() })
                }

                composable(
                    route = Routes.Exercise.value,
                    arguments = Routes.Exercise.navArguments,
                ) {
                    Exercise()
                }
            }
        }
    }
}
