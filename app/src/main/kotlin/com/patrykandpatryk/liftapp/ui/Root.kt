@file:OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)

package com.patrykandpatryk.liftapp.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patrykandpatryk.liftapp.bodyrecord.ui.InsertBodyRecord
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.exercise.ui.Exercise
import com.patrykandpatryk.liftapp.feature.main.ui.Home
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExercise
import com.patrykandpatryk.liftapp.feature.onerepmax.ui.OneRepMax
import com.patrykandpatryk.liftapp.feature.settings.ui.Settings
import kotlinx.coroutines.launch

@Composable
fun Root(modifier: Modifier = Modifier) {

    val bottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        SwipeableDefaults.AnimationSpec,
    )

    val bottomSheetNavigator = rememberBottomSheetNavigator(bottomSheetState)
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
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetContentColor = MaterialTheme.colorScheme.onSurface,
            sheetShape = BottomSheetShape,
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

                bottomSheet(
                    route = Routes.InsertBodyRecord.value,
                    arguments = Routes.InsertBodyRecord.navArguments,
                ) {
                    val scope = rememberCoroutineScope()

                    InsertBodyRecord(
                        onCloseClick = { scope.launch { bottomSheetState.hide() } },
                    )
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

@Composable
fun rememberBottomSheetNavigator(
    sheetState: ModalBottomSheetState,
): BottomSheetNavigator = remember(sheetState) {
    BottomSheetNavigator(sheetState = sheetState)
}
