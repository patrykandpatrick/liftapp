package com.patrykandpatrick.liftapp.navigation

import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.createGraph
import androidx.savedstate.SavedState
import androidx.test.core.app.ApplicationProvider
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.ui.addNestedHomeGraph
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [36])
class HomeTabNavigationTest {
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        navController = createNavController()
    }

    @Test
    fun `week opens after plan was selected from bottom bar`() {
        navController.navigateToHomeTab(Routes.HomeTab.Plan)

        navController.navigateToHomeTab(Routes.HomeTab.Dashboard)

        assertCurrentRoute<Routes.Home.Dashboard>()
    }

    @Test
    fun `week opens after plan was selected from dashboard content`() {
        navController.navigateTo(NavigationCommand.Route(Routes.Home.Plan))

        navController.navigateToHomeTab(Routes.HomeTab.Dashboard)

        assertCurrentRoute<Routes.Home.Dashboard>()
    }

    @Test
    fun `repeated week taps do not restore plan`() {
        navController.navigateTo(NavigationCommand.Route(Routes.Home.Plan))

        repeat(3) {
            navController.navigateToHomeTab(Routes.HomeTab.Dashboard)
            assertCurrentRoute<Routes.Home.Dashboard>()
        }
    }

    @Test
    fun `three tabs restore their own back stack entries`() {
        navController.navigateToHomeTab(Routes.HomeTab.Plan)
        val planEntryID = navController.currentBackStackEntry?.id
        navController.navigateToHomeTab(Routes.HomeTab.Exercises)
        val exercisesEntryID = navController.currentBackStackEntry?.id
        navController.navigateToHomeTab(Routes.HomeTab.Dashboard)

        navController.navigateToHomeTab(Routes.HomeTab.Plan)
        assertCurrentRoute<Routes.Home.Plan>()
        assertEquals(planEntryID, navController.currentBackStackEntry?.id)

        navController.navigateToHomeTab(Routes.HomeTab.Exercises)
        assertCurrentRoute<Routes.Home.Exercises>()
        assertEquals(exercisesEntryID, navController.currentBackStackEntry?.id)
    }

    @Test
    fun `back from each nondefault home tab opens week`() {
        listOf(
                Routes.HomeTab.Plan,
                Routes.HomeTab.Exercises,
                Routes.HomeTab.BodyMeasurements,
                Routes.HomeTab.More,
            )
            .forEach { tab ->
                navController.navigateToHomeTab(tab)

                assertTrue(navController.navigateBackToDefaultHomeTab())
                assertCurrentRoute<Routes.Home.Dashboard>()
            }
    }

    @Test
    fun `back from week is left for the system to handle`() {
        assertTrue(!navController.navigateBackToDefaultHomeTab())

        assertCurrentRoute<Routes.Home.Dashboard>()
    }

    @Test
    fun `tab destination command ignores conflicting navigation options`() {
        navController.navigateTo(
            NavigationCommand.Route(
                route = Routes.Home.Plan,
                popUpTo = Routes.Home,
                launchSingleTop = true,
            )
        )

        assertCurrentRoute<Routes.Home.Plan>()
        assertCurrentTab<Routes.HomeTab.Plan>()
    }

    @Test
    fun `exercise picker route is not treated as the exercises tab`() {
        assertNull(homeTabRouteFor(Routes.Exercise.pick("result")))
    }

    @Test
    fun `non-tab command keeps its pop up to options`() {
        navController.navigateToHomeTab(Routes.HomeTab.Plan)

        navController.navigateTo(
            NavigationCommand.Route(
                route = Routes.Journal,
                popUpTo = Routes.Home,
                launchSingleTop = true,
            )
        )

        assertCurrentRoute<Routes.Journal>()
        assertTrue(runCatching { navController.getBackStackEntry(Routes.Home.Plan) }.isFailure)
    }

    @Test
    fun `tab navigation rebuilds home graph when dashboard entry is missing`() {
        navController.popBackStack(Routes.Home.Dashboard, inclusive = true)

        navController.navigateToHomeTab(Routes.HomeTab.Plan)

        assertCurrentRoute<Routes.Home.Plan>()
        assertCurrentTab<Routes.HomeTab.Plan>()
    }

    @Test
    fun `current tab graph survives process state restoration`() {
        navController.navigateToHomeTab(Routes.HomeTab.Plan)
        val savedState = checkNotNull(navController.saveState())
        val restoredController = createNavController(restoredState = savedState)

        assertTrue(restoredController.currentDestination?.hasRoute<Routes.Home.Plan>() == true)
        restoredController.navigateToHomeTab(Routes.HomeTab.Dashboard)

        assertTrue(restoredController.currentDestination?.hasRoute<Routes.Home.Dashboard>() == true)
    }

    private fun createNavController(restoredState: SavedState? = null): NavController {
        val lifecycleOwner = TestLifecycleOwner()
        return NavController(ApplicationProvider.getApplicationContext<Context>()).apply {
            navigatorProvider.addNavigator(BottomAppBarNavigator())
            setLifecycleOwner(lifecycleOwner)
            restoreState(restoredState)
            graph =
                createGraph(startDestination = Routes.Home) {
                    addNestedHomeGraph()
                    bottomAppBarComposable(Routes.Journal::class) { _, _ -> }
                }
            lifecycleOwner.registry.currentState = Lifecycle.State.RESUMED
        }
    }

    private inline fun <reified T : Any> assertCurrentRoute() {
        val destination = navController.currentDestination
        assertTrue(
            destination?.hasRoute<T>() == true,
            "Expected ${T::class.qualifiedName}, but current destination was ${destination?.route}",
        )
    }

    private inline fun <reified T : Any> assertCurrentTab() {
        val destination = navController.currentDestination
        assertTrue(
            destination?.hierarchy?.any { it.hasRoute<T>() } == true,
            "Expected tab ${T::class.qualifiedName}, but current hierarchy was " +
                destination?.hierarchy?.joinToString { it.route.orEmpty() },
        )
    }

    private class TestLifecycleOwner : LifecycleOwner {
        val registry = LifecycleRegistry(this)

        override val lifecycle: Lifecycle = registry
    }
}
