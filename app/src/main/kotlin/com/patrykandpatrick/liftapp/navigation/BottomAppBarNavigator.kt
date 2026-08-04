package com.patrykandpatrick.liftapp.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
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
import com.patrykandpatrick.liftapp.core.ui.animation.ENTER_ANIM_DURATION
import com.patrykandpatrick.liftapp.core.ui.animation.ENTER_TRANSITION_FADE_HEIGHT_DIVIDER
import com.patrykandpatrick.liftapp.core.ui.animation.ENTER_TRANSITION_SPRING_DAMPING_RATIO
import com.patrykandpatrick.liftapp.core.ui.animation.EXIT_ANIM_DURATION
import com.patrykandpatrick.liftapp.navigation.BottomAppBarNavigator.Companion.NAME
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun rememberBottomAppBarNavigator(): BottomAppBarNavigator = remember { BottomAppBarNavigator() }

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
        val initialEntryID = remember { entry?.id }
        var hasLeftInitialEntry by remember { mutableStateOf(false) }
        val animateEntrance = hasLeftInitialEntry || entry?.id != initialEntryID

        SideEffect { if (entry?.id != initialEntryID) hasLeftInitialEntry = true }

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
                    // TabEntrance owns the target's fade and vertical motion. AnimatedContent only
                    // keeps the old target around long enough to fade it out.
                    EnterTransition.None togetherWith
                        fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION)) using
                        null
                },
                modifier = Modifier.fillMaxSize(),
            ) { backStackEntry ->
                backStackEntry?.let {
                    TabEntrance(
                        isTarget = it.id == entry?.id,
                        animate = animateEntrance,
                    ) {
                        it.LocalOwnersProvider(saveableStateHolder) {
                            val content = (it.destination as Destination).content
                            content(it, paddingValues)
                        }
                    }
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

@Composable
internal fun TabEntrance(
    isTarget: Boolean,
    animate: Boolean,
    providedAnimationState: TabEntranceAnimationState? = null,
    content: @Composable () -> Unit,
) {
    var height by remember { mutableIntStateOf(0) }
    val rememberedAnimationState = remember {
        TabEntranceAnimationState(startAtEntrance = isTarget && animate)
    }
    val animationState = providedAnimationState ?: rememberedAnimationState
    val coroutineScope = rememberCoroutineScope()
    var entranceJob by remember { mutableStateOf<Job?>(null) }
    var wasTarget by remember { mutableStateOf(isTarget) }
    val isRestarting = isTarget && !wasTarget && animate

    SideEffect { wasTarget = isTarget }

    LaunchedEffect(isTarget, animate) {
        if (isTarget && animate) {
            entranceJob?.cancelAndJoin()
            entranceJob = coroutineScope.launch { animationState.restart() }
        }
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .onSizeChanged { height = it.height }
                .graphicsLayer {
                    // Hide a reused outgoing child synchronously, before the effect above resets
                    // its spring on the next frame.
                    alpha = if (isRestarting) 0f else animationState.alpha.value
                    translationY =
                        height * animationState.offsetFraction.value /
                            ENTER_TRANSITION_FADE_HEIGHT_DIVIDER
                }
    ) {
        content()
    }
}

internal class TabEntranceAnimationState(startAtEntrance: Boolean) {
    val offsetFraction = Animatable(if (startAtEntrance) 1f else 0f)
    val alpha = Animatable(if (startAtEntrance) 0f else 1f)

    suspend fun restart() {
        // A reversing AnimatedContent transition reuses its outgoing child. Mask it before
        // resetting the offset so an overshooting spring never visibly jumps from above its
        // resting point to a full entrance distance below it.
        alpha.snapTo(0f)
        offsetFraction.snapTo(1f)
        coroutineScope {
            launch {
                offsetFraction.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = ENTER_TRANSITION_SPRING_DAMPING_RATIO),
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = ENTER_ANIM_DURATION),
                )
            }
        }
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
