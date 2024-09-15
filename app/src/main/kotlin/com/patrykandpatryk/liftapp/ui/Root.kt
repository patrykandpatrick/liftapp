package com.patrykandpatryk.liftapp.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.toRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.navigation.BodyMeasurementDetailsNavigator
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui.BodyMeasurementDetailScreen
import com.patrykandpatryk.liftapp.feature.exercise.navigation.ExerciseDetailsNavigator
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExerciseDetails
import com.patrykandpatryk.liftapp.feature.exercises.navigation.ExerciseListNavigator
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExerciseListScreen
import com.patrykandpatryk.liftapp.feature.main.ui.Home
import com.patrykandpatryk.liftapp.feature.newexercise.navigation.NewExerciseNavigator
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExercise
import com.patrykandpatryk.liftapp.feature.newroutine.navigation.NewRoutineNavigator
import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutineScreen
import com.patrykandpatryk.liftapp.feature.onerepmax.OneRepMaxNavigator
import com.patrykandpatryk.liftapp.feature.onerepmax.OneRepMaxScreen
import com.patrykandpatryk.liftapp.feature.routine.navigator.RoutineNavigator
import com.patrykandpatryk.liftapp.feature.routine.ui.RoutineScreen
import com.patrykandpatryk.liftapp.feature.settings.navigator.SettingsNavigator
import com.patrykandpatryk.liftapp.feature.settings.ui.Settings
import com.patrykandpatryk.liftapp.navigation.rememberMainNavigator
import com.patrykandpatryk.liftapp.newbodymeasuremententry.ui.NewBodyMeasurementEntryBottomSheet
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
fun Root(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val systemUiController = rememberSystemUiController()
    val mainNavigator = rememberMainNavigator(navController)

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = darkTheme.not(),
            isNavigationBarContrastEnforced = false,
        )
    }

    LiftAppTheme(darkTheme = darkTheme) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = BottomSheetShape,
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
                modifier = modifier,
            ) {
                addHome(navController)
                addAbout()
                addSettings(mainNavigator)
                addOneRepMax(mainNavigator)
                addNewRoutine(mainNavigator)
                addBodyMeasurementDetailDestination(mainNavigator)
                addNewBodyMeasurementDestination(mainNavigator::back)
                addNewExercise(mainNavigator)
                addExerciseDetails(mainNavigator)
                addExercises(mainNavigator)
                addRoutine(mainNavigator)
            }
        }
    }
}

fun NavGraphBuilder.addHome(mainNavController: NavController) {
    composable<Routes.Home> {
        Home(mainNavController)
    }
}

fun NavGraphBuilder.addRoutine(navigator: RoutineNavigator) {
    composable<Routes.Routine.Details> {
        val args = it.toRoute<Routes.Routine.Details>()
        RoutineScreen(args.routineID, navigator)
    }
}

fun NavGraphBuilder.addNewRoutine(navigator: NewRoutineNavigator) {
    composable<Routes.Routine.Create> {
        val args = it.toRoute<Routes.Routine.Create>()
        NewRoutineScreen(args.routineID, navigator)
    }
}

fun NavGraphBuilder.addExercises(navigator: ExerciseListNavigator) {
    composable<Routes.Exercise.List> {
        val args = it.toRoute<Routes.Exercise.List>()
        ExerciseListScreen(args.pickingMode, args.disabledExerciseIDs, navigator)
    }
}

fun NavGraphBuilder.addExerciseDetails(navigator: ExerciseDetailsNavigator) {
    composable<Routes.Exercise.Details> {
        val args = it.toRoute<Routes.Exercise.Details>()
        ExerciseDetails(args.exerciseID, navigator)
    }
}

fun NavGraphBuilder.addNewExercise(navigator: NewExerciseNavigator) {
    composable<Routes.Exercise.Create> {
        val args = it.toRoute<Routes.Exercise.Create>()
        NewExercise(args.exerciseID, navigator)
    }
}

fun NavGraphBuilder.addBodyMeasurementDetailDestination(navigator: BodyMeasurementDetailsNavigator) {
    composable<Routes.BodyMeasurement.Details> {
        val args = it.toRoute<Routes.BodyMeasurement.Details>()
        BodyMeasurementDetailScreen(args.bodyMeasurementID, navigator)
    }
}

fun NavGraphBuilder.addNewBodyMeasurementDestination(onDismissRequest: () -> Unit) {
    bottomSheet<Routes.BodyMeasurement.Create> { backStackEntry ->
        val args = backStackEntry.toRoute<Routes.BodyMeasurement.Create>()
        NewBodyMeasurementEntryBottomSheet(
            bodyMeasurementId = args.bodyMeasurementID,
            bodyMeasurementEntryId = args.bodyMeasurementEntryID.takeIf { it != ID_NOT_SET },
            onDismissRequest = onDismissRequest,
        )
    }
}

fun NavGraphBuilder.addAbout() {
    composable<Routes.About> {
        About()
    }
}

fun NavGraphBuilder.addSettings(navigator: SettingsNavigator) {
    composable<Routes.Settings> {
        Settings(navigator)
    }
}

fun NavGraphBuilder.addOneRepMax(navigator: OneRepMaxNavigator) {
    composable<Routes.OneRepMax> {
        OneRepMaxScreen(navigator)
    }
}

inline fun <reified T : Any> NavGraphBuilder.bottomSheet(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit
) {
    addDestination(
        BottomSheetNavDestinationBuilder(
            provider[BottomSheetNavigator::class],
            T::class,
            typeMap,
            deepLinks,
            content,
        ).build()
    )
}

class BottomSheetNavDestinationBuilder(
    private val bottomSheetNavigator: BottomSheetNavigator,
    route: KClass<*>,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    deepLinks: List<NavDeepLink>,
    private val content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit
) : NavDestinationBuilder<BottomSheetNavigator.Destination>(bottomSheetNavigator, route, typeMap) {
    init {
        deepLinks.fastForEach { deepLink ->
            deepLink(deepLink)
        }
    }

    override fun instantiateDestination(): BottomSheetNavigator.Destination =
        BottomSheetNavigator.Destination(bottomSheetNavigator, content)
}
