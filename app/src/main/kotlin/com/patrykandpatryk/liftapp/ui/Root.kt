package com.patrykandpatryk.liftapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.get
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patrykandpatryk.liftapp.bodyentry.ui.InsertBodyEntry
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.bottomSheet
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.provider.ProvideNavHost
import com.patrykandpatryk.liftapp.core.ui.dimens.DialogDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.bodydetails.ui.BodyDetails
import com.patrykandpatryk.liftapp.feature.exercise.ui.Exercise
import com.patrykandpatryk.liftapp.feature.exercises.ui.Exercises
import com.patrykandpatryk.liftapp.feature.main.ui.Home
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExercise
import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutine
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

            ProvideNavHost(navHostController = navController) {

                AnimatedNavHost(
                    navController = navController,
                    startDestination = Routes.Home.value,
                ) {

                    composable(route = Routes.Home) {
                        Home(parentNavController = navController)
                    }

                    composable(route = Routes.About) {
                        About()
                    }

                    composable(route = Routes.Settings) {
                        Settings(parentNavController = navController)
                    }

                    composable(route = Routes.OneRepMax) {
                        OneRepMax(parentNavController = navController)
                    }

                    composable(route = Routes.NewRoutine) {
                        NewRoutine(
                            popBackStack = navController::popBackStack,
                            navigate = navController::navigate,
                        )
                    }

                    composable(route = Routes.BodyDetails) {
                        BodyDetails(
                            navigate = navController::navigate,
                            navigateBack = navController::popBackStack,
                        )
                    }

                    bottomSheet(route = Routes.InsertBodyEntry) {
                        val scope = rememberCoroutineScope()

                        InsertBodyEntry(
                            onCloseClick = { scope.launch { bottomSheetState.hide() } },
                        )
                    }

                    composable(route = Routes.NewExercise) {
                        NewExercise(popBackStack = { navController.popBackStack() })
                    }

                    composable(route = Routes.Exercise) {
                        Exercise()
                    }

                    composable(route = Routes.Exercises) {
                        Exercises(
                            navigate = navController::navigate,
                            navigateBack = navController::popBackStack,
                        )
                    }
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

@ExperimentalMaterialNavigationApi
fun NavGraphBuilder.bottomSheet(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    addDestination(
        BottomSheetNavigator.Destination(
            navigator = provider[BottomSheetNavigator::class],
            content = { backStackEntry ->
                CompositionLocalProvider(LocalDimens provides DialogDimens) {
                    content(this, backStackEntry)
                }
            },
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) -> addArgument(argumentName, argument) }
            deepLinks.forEach(::addDeepLink)
        },
    )
}
