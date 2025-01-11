package com.patrykandpatryk.liftapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalNavigator
import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalRouteData
import com.patrykandpatrick.feature.exercisegoal.ui.ExerciseGoalScreen
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutNavigator
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutRouteData
import com.patrykandpatrick.liftapp.feature.workout.ui.WorkoutScreen
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.deeplink.DeepLink
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisExitTransition
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
import com.patrykandpatryk.liftapp.feature.main.navigation.HomeNavigator
import com.patrykandpatryk.liftapp.feature.main.ui.HomeScreen
import com.patrykandpatryk.liftapp.feature.newexercise.navigation.NewExerciseNavigator
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExercise
import com.patrykandpatryk.liftapp.feature.newroutine.navigation.NewRoutineNavigator
import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutineScreen
import com.patrykandpatryk.liftapp.feature.onerepmax.OneRepMaxNavigator
import com.patrykandpatryk.liftapp.feature.onerepmax.OneRepMaxScreen
import com.patrykandpatryk.liftapp.feature.routine.ui.RoutineScreen
import com.patrykandpatryk.liftapp.feature.settings.navigator.SettingsNavigator
import com.patrykandpatryk.liftapp.feature.settings.ui.Settings
import com.patrykandpatryk.liftapp.navigation.MainNavigator
import com.patrykandpatryk.liftapp.navigation.rememberHomeNavigator
import com.patrykandpatryk.liftapp.navigation.rememberMainNavigator
import com.patrykandpatryk.liftapp.newbodymeasuremententry.ui.NewBodyMeasurementEntryBottomSheet
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
fun Root(modifier: Modifier = Modifier, darkTheme: Boolean) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val mainNavigator = rememberMainNavigator(navController)
    val homeNavigator = rememberHomeNavigator(navController)

    LiftAppTheme(darkTheme = darkTheme) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = BottomSheetShape,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
                modifier = modifier.background(MaterialTheme.colorScheme.background),
                enterTransition = { sharedXAxisEnterTransition() },
                exitTransition = { sharedXAxisExitTransition() },
                popEnterTransition = { sharedXAxisEnterTransition(forward = false) },
                popExitTransition = { sharedXAxisExitTransition(forward = false) },
            ) {
                addHome(homeNavigator)
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
                addRoutineExerciseGoal(mainNavigator)
                addWorkout(mainNavigator)
            }
        }
    }
}

fun NavGraphBuilder.addHome(homeNavigator: HomeNavigator) {
    composable<Routes.Home> { HomeScreen(homeNavigator) }
}

fun NavGraphBuilder.addRoutine(mainNavigator: MainNavigator) {
    composable<Routes.Routine.Details> {
        val args = it.toRoute<Routes.Routine.Details>()
        RoutineScreen(args.routineID, mainNavigator.getRoutineNavigator(args.routineID))
    }
}

fun NavGraphBuilder.addRoutineExerciseGoal(navigator: ExerciseGoalNavigator) {
    composable<ExerciseGoalRouteData> { ExerciseGoalScreen(navigator) }
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

fun NavGraphBuilder.addBodyMeasurementDetailDestination(
    navigator: BodyMeasurementDetailsNavigator
) {
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
    composable<Routes.About> { About() }
}

fun NavGraphBuilder.addSettings(navigator: SettingsNavigator) {
    composable<Routes.Settings> { Settings(navigator) }
}

fun NavGraphBuilder.addOneRepMax(navigator: OneRepMaxNavigator) {
    composable<Routes.OneRepMax> { OneRepMaxScreen(navigator) }
}

fun NavGraphBuilder.addWorkout(navigator: WorkoutNavigator) {
    composable<WorkoutRouteData>(
        deepLinks = listOf(navDeepLink { uriPattern = DeepLink.WorkoutRoute.uri })
    ) { backStackEntry ->
        WorkoutScreen(navigator = navigator)
    }
}

inline fun <reified T : Any> NavGraphBuilder.bottomSheet(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    addDestination(
        BottomSheetNavDestinationBuilder(
                provider[BottomSheetNavigator::class],
                T::class,
                typeMap,
                deepLinks,
                content,
            )
            .build()
    )
}

class BottomSheetNavDestinationBuilder(
    private val bottomSheetNavigator: BottomSheetNavigator,
    route: KClass<*>,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    deepLinks: List<NavDeepLink>,
    private val content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) : NavDestinationBuilder<BottomSheetNavigator.Destination>(bottomSheetNavigator, route, typeMap) {
    init {
        deepLinks.fastForEach { deepLink -> deepLink(deepLink) }
    }

    override fun instantiateDestination(): BottomSheetNavigator.Destination =
        BottomSheetNavigator.Destination(bottomSheetNavigator, content)
}
