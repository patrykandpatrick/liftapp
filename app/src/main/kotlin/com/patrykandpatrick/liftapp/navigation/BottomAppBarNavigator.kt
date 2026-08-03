package com.patrykandpatrick.liftapp.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.get
import com.patrykandpatrick.liftapp.core.ui.animation.EXIT_ANIM_DURATION
import com.patrykandpatrick.liftapp.core.ui.animation.slideAndFadeIn
import com.patrykandpatrick.liftapp.navigation.BottomAppBarNavigator.Companion.NAME
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

@Composable
fun rememberBottomAppBarNavigator(): BottomAppBarNavigator {
    return remember { BottomAppBarNavigator() }
}

@Navigator.Name(NAME)
class BottomAppBarNavigator : Navigator<BottomAppBarNavigator.Destination>() {
    override fun createDestination(): Destination = Destination(this, { _, _ -> })

    val currentDestination: Flow<NavBackStackEntry?>
        get() =
            if (isAttached) {
                state.backStack.map { it.lastOrNull() }
            } else {
                emptyFlow()
            }

    @Composable
    internal fun Content(
        saveableStateHolder: SaveableStateHolder,
        navigationBar: @Composable () -> Unit,
    ) {
        // Collecting the back stack starts from the entry already on it, so the first composition
        // renders the current tab outright. Starting from `null` instead made that entry arrive as
        // a change, which played the tab-change transition on launch.
        val backStack by state.backStack.collectAsState()
        val entry = backStack.lastOrNull()

        LiftAppScaffold(
            bottomBar = navigationBar,
            contentWindowInsets = WindowInsets.navigationBars,
        ) { paddingValues ->
            AnimatedContent(
                targetState = entry,
                // Restoring an entry can create a new NavBackStackEntry instance with the same ID.
                // LocalOwnersProvider uses that ID as its saveable-state key, so treating the
                // wrapper instance as new can compose the same key twice during an interruption.
                contentKey = { it?.id },
                transitionSpec = {
                    // See `BottomAppBarNavigationHost` for why the size transform is opted out of.
                    slideAndFadeIn() togetherWith
                        fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION)) using
                        null
                },
                modifier = Modifier.fillMaxSize(),
            ) { backStackEntry ->
                backStackEntry?.LocalOwnersProvider(saveableStateHolder) {
                    val content = (backStackEntry.destination as Destination).content
                    content(backStackEntry, paddingValues)
                }
            }
        }
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: BottomAppBarNavigator,
        internal val content: @Composable (NavBackStackEntry, PaddingValues) -> Unit,
    ) : NavDestination(navigator)

    companion object {
        const val NAME = "bottomAppBar"
    }
}

class BottomAppBarDestinationBuilder(
    private val bottomAppBarNavigator: BottomAppBarNavigator,
    route: KClass<*>,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    deepLinks: List<NavDeepLink>,
    private val content: @Composable (NavBackStackEntry, PaddingValues) -> Unit,
) :
    NavDestinationBuilder<BottomAppBarNavigator.Destination>(
        bottomAppBarNavigator,
        route,
        typeMap,
    ) {
    init {
        deepLinks.fastForEach { deepLink -> deepLink(deepLink) }
    }

    override fun instantiateDestination(): BottomAppBarNavigator.Destination =
        BottomAppBarNavigator.Destination(bottomAppBarNavigator, content)
}

fun NavGraphBuilder.bottomAppBarComposable(
    route: KClass<*>,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry, PaddingValues) -> Unit,
) {
    addDestination(
        BottomAppBarDestinationBuilder(
                bottomAppBarNavigator = provider[BottomAppBarNavigator::class],
                route = route,
                typeMap = typeMap,
                deepLinks = deepLinks,
                content = content,
            )
            .build()
    )
}
