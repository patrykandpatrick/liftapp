package com.patrykandpatryk.liftapp.ui

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
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.provider.ProvideNavHost
import com.patrykandpatryk.liftapp.core.ui.dimens.DialogDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
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
import com.patrykandpatryk.liftapp.newbodymeasuremententry.ui.addNewBodyMeasurementEntryDestination

@Composable
fun Root(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
) {

    val bottomSheetState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        SwipeableDefaults.AnimationSpec,
    )

    val bottomSheetNavigator = rememberBottomSheetNavigator(bottomSheetState)
    val navController = rememberAnimatedNavController(bottomSheetNavigator)
    val systemUiController = rememberSystemUiController()

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

                    addHome()

                    addAbout()

                    addSettings()

                    addOneRepMax()

                    addNewRoutine()

                    addBodyMeasurementDetailDestination()

                    addNewBodyMeasurementEntryDestination(bottomSheetState = bottomSheetState)

                    addNewExercise()

                    addExerciseDetails()

                    addExercises()

                    addRoutine()
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
