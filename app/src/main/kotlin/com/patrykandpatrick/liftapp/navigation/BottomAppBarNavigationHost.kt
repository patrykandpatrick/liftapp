package com.patrykandpatrick.liftapp.navigation

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.patrykandpatrick.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatrick.liftapp.core.ui.animation.sharedXAxisExitTransition

@Composable
fun BottomAppBarNavigationHost(
    navController: NavHostController,
    navigator: BottomAppBarNavigator,
    navigationBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Keep this holder above the boundary animation. Bottom-app-bar content leaves composition
    // while a full-screen destination is open, but its navigation entries remain on the back stack
    // and must retain their saveable UI state until they are popped permanently.
    val saveableStateHolder = rememberSaveableStateHolder()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val isBottomAppBarNavigator =
        currentBackStackEntry?.destination?.navigatorName == BottomAppBarNavigator.NAME
    val previousBackStackEntry =
        remember(currentBackStackEntry) { navController.previousBackStackEntry }
    val wasBottomAppBarNavigator =
        previousBackStackEntry?.destination?.navigatorName == BottomAppBarNavigator.NAME
    val crossesBottomAppBarBoundary =
        previousBackStackEntry != null && isBottomAppBarNavigator != wasBottomAppBarNavigator

    val transitionState = remember {
        SeekableTransitionState(initialState = isBottomAppBarNavigator)
    }
    val transition = rememberTransition(transitionState, label = "navigation")
    var predictiveBackProgress by remember { mutableFloatStateOf(0f) }
    var isPredictiveBackInProgress by remember { mutableStateOf(false) }
    var hasResolvedFirstEntry by remember { mutableStateOf(false) }

    PredictiveBackHandler(enabled = crossesBottomAppBarBoundary) { backEvents ->
        predictiveBackProgress = 0f

        try {
            backEvents.collect { backEvent ->
                isPredictiveBackInProgress = true
                predictiveBackProgress = backEvent.progress
            }
            navController.popBackStack()
        } finally {
            isPredictiveBackInProgress = false
        }
    }

    if (isPredictiveBackInProgress) {
        LaunchedEffect(predictiveBackProgress, wasBottomAppBarNavigator) {
            transitionState.seekTo(
                fraction = predictiveBackProgress,
                targetState = wasBottomAppBarNavigator,
            )
        }
    } else {
        LaunchedEffect(isBottomAppBarNavigator, currentBackStackEntry != null) {
            // There is no back stack entry to read until the `NavHost` this hosts has composed and
            // set its graph, so the state above was seeded with a guess. Snapping to the first real
            // entry keeps launch from playing a transition that nothing navigated to; only the
            // changes after it are animated.
            if (currentBackStackEntry == null) return@LaunchedEffect
            if (hasResolvedFirstEntry) {
                transitionState.animateTo(isBottomAppBarNavigator)
            } else {
                transitionState.snapTo(isBottomAppBarNavigator)
                hasResolvedFirstEntry = true
            }
        }
    }

    transition.AnimatedContent(
        transitionSpec = {
            // Leaving the bottom-app-bar navigator opens a destination; returning to it goes back.
            val forward = initialState && !targetState
            // `using null` opts out of the size transform `ContentTransform` defaults to, which
            // would otherwise animate the container from the size the outgoing side happens to have
            // been measured at. On the first frame that size is zero, so the whole screen grew out
            // of the top-left corner behind a clipping rectangle.
            sharedXAxisEnterTransition(forward = forward) togetherWith
                sharedXAxisExitTransition(forward = forward) using
                null
        },
        modifier = modifier.fillMaxSize(),
    ) { isBottomAppBarNavigator ->
        if (isBottomAppBarNavigator) {
            navigator.Content(
                saveableStateHolder = saveableStateHolder,
                navigationBar = navigationBar,
            )
        } else {
            content()
        }
    }
}
