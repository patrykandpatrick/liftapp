package com.patrykandpatryk.liftapp.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.patrykandpatrick.feature.exercisegoal.ui.ExerciseGoalScreen
import com.patrykandpatrick.liftapp.feature.plan.configurator.ui.PlanConfiguratorScreen
import com.patrykandpatrick.liftapp.feature.workout.ui.WorkoutScreen
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewExerciseRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanConfiguratorRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanCreatorRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanListRouteData
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.RoutineListRouteData
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import com.patrykandpatrick.liftapp.plan.creator.ui.PlanCreatorScreen
import com.patrykandpatrick.liftapp.plan.list.ui.PlanListScreen
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.deeplink.DeepLink
import com.patrykandpatryk.liftapp.core.format.LocalFormatter
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.animation.EXIT_ANIM_DURATION
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisExitTransition
import com.patrykandpatryk.liftapp.core.ui.animation.slideAndFadeIn
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.about.ui.About
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui.BodyMeasurementDetailScreen
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExerciseDetailsScreen
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExerciseListScreen
import com.patrykandpatryk.liftapp.feature.newexercise.ui.NewExerciseScreen
import com.patrykandpatryk.liftapp.feature.newroutine.ui.NewRoutineScreen
import com.patrykandpatryk.liftapp.feature.onerepmax.OneRepMaxScreen
import com.patrykandpatryk.liftapp.feature.routine.ui.RoutineScreen
import com.patrykandpatryk.liftapp.feature.routines.ui.RoutineListScreen
import com.patrykandpatryk.liftapp.feature.settings.ui.SettingsScreen
import com.patrykandpatryk.liftapp.navigation.BottomAppBarNavigationHost
import com.patrykandpatryk.liftapp.navigation.bottomAppBarComposable
import com.patrykandpatryk.liftapp.navigation.navigationBarItems
import com.patrykandpatryk.liftapp.navigation.rememberBottomAppBarNavigator
import com.patrykandpatryk.liftapp.newbodymeasuremententry.ui.NewBodyMeasurementEntryScreen
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Composable
fun Root(
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
    navigationCommander: NavigationCommander,
) {
    val viewModel: RootViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val bottomAppBarNavigator = rememberBottomAppBarNavigator()
    val navController = rememberNavController(bottomSheetNavigator, bottomAppBarNavigator)
    navigationCommander.HandleCommands(navController)

    LiftAppTheme(darkTheme = darkTheme) {
        val markupProcessor = rememberDefaultMarkupProcessor()

        CompositionLocalProvider(
            LocalMarkupProcessor provides markupProcessor,
            LocalFormatter provides viewModel.formatter,
        ) {
            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetShape = BottomSheetShape,
                sheetBackgroundColor = colorScheme.surface,
            ) {
                BottomAppBarNavigationHost(
                    navController = navController,
                    navigator = bottomAppBarNavigator,
                    navigationBar = {
                        BottomNavigationBar(
                            navController = navController,
                            navigator = bottomAppBarNavigator,
                            navItemRoutes = navigationBarItems,
                        )
                    },
                    content = {
                        NavHost(
                            navController = navController,
                            startDestination = Routes.Home,
                            modifier = Modifier.background(colorScheme.background),
                            enterTransition = { sharedXAxisEnterTransition() },
                            exitTransition = { sharedXAxisExitTransition() },
                            popEnterTransition = { sharedXAxisEnterTransition(forward = false) },
                            popExitTransition = { sharedXAxisExitTransition(forward = false) },
                        ) {
                            addAbout()
                            addBodyMeasurementDetailDestination()
                            addExercises()
                            addExerciseDetails()
                            addNestedHomeGraph()
                            addNewBodyMeasurementDestination()
                            addNewExercise()
                            addPlanCreator()
                            addPlanList()
                            addPlanConfigurator()
                            addNewRoutine()
                            addOneRepMax()
                            addRoutine()
                            addRoutineList()
                            addRoutineExerciseGoal()
                            addSettings()
                            addWorkout()
                        }
                    },
                    modifier = modifier.background(colorScheme.background),
                )
            }
        }
    }
}

@Composable
private fun NavigationCommander.HandleCommands(navController: NavController) {
    LaunchedEffect(this) {
        navigationCommand.collect { command ->
            when (command) {
                is NavigationCommand.Route -> {
                    navController.navigate(
                        route = command.route,
                        navOptions =
                            navOptions {
                                command.popUpTo?.also {
                                    popUpTo(it)
                                    launchSingleTop = command.launchSingleTop
                                }
                            },
                    )
                }

                is NavigationCommand.PopBackStack -> {
                    val route = command.route
                    if (route != null) {
                        navController.popBackStack(route, command.inclusive, command.saveState)
                    } else {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.addNestedHomeGraph(modifier: Modifier = Modifier) {
    navigation(
        startDestination = Routes.Home.Dashboard::class,
        route = Routes.Home::class,
        enterTransition = { slideAndFadeIn() },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION)) },
        popEnterTransition = { sharedXAxisEnterTransition(forward = false) },
        popExitTransition = { sharedXAxisExitTransition(forward = false) },
    ) {
        navigationBarItems.forEach { item ->
            bottomAppBarComposable(item.route::class, item.typeMap) {
                navBackStackEntry,
                paddingValues ->
                item.content(modifier.padding(paddingValues))
            }
        }
    }
}

fun NavGraphBuilder.addRoutine() {
    composable<RoutineDetailsRouteData> { RoutineScreen() }
}

fun NavGraphBuilder.addRoutineList() {
    composable<RoutineListRouteData> { RoutineListScreen() }
}

fun NavGraphBuilder.addRoutineExerciseGoal() {
    composable<ExerciseGoalRouteData> { ExerciseGoalScreen() }
}

fun NavGraphBuilder.addPlanCreator() {
    composable<PlanCreatorRouteData> { PlanCreatorScreen() }
}

fun NavGraphBuilder.addPlanList() {
    composable<PlanListRouteData> { PlanListScreen() }
}

fun NavGraphBuilder.addPlanConfigurator() {
    composable<PlanConfiguratorRouteData> { PlanConfiguratorScreen() }
}

fun NavGraphBuilder.addNewRoutine() {
    composable<NewRoutineRouteData> { NewRoutineScreen() }
}

fun NavGraphBuilder.addExercises() {
    composable<ExerciseListRouteData>(typeMap = ExerciseListRouteData.typeMap) {
        ExerciseListScreen()
    }
}

fun NavGraphBuilder.addExerciseDetails() {
    composable<ExerciseDetailsRouteData> { ExerciseDetailsScreen() }
}

fun NavGraphBuilder.addNewExercise() {
    composable<NewExerciseRouteData> { NewExerciseScreen() }
}

fun NavGraphBuilder.addBodyMeasurementDetailDestination() {
    composable<BodyMeasurementDetailsRouteData> { BodyMeasurementDetailScreen() }
}

fun NavGraphBuilder.addNewBodyMeasurementDestination() {
    composable<NewBodyMeasurementRouteData> { NewBodyMeasurementEntryScreen() }
}

fun NavGraphBuilder.addAbout() {
    composable<Routes.About> { About() }
}

fun NavGraphBuilder.addSettings() {
    composable<Routes.Settings> { SettingsScreen() }
}

fun NavGraphBuilder.addOneRepMax() {
    composable<Routes.OneRepMax> { OneRepMaxScreen() }
}

fun NavGraphBuilder.addWorkout() {
    composable<WorkoutRouteData>(
        deepLinks = listOf(navDeepLink { uriPattern = DeepLink.WorkoutRoute.uri })
    ) {
        WorkoutScreen()
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
