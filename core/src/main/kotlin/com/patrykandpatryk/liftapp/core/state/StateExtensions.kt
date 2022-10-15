package com.patrykandpatryk.liftapp.core.state

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T : InteractionSource> T.onClick(onClick: suspend () -> Unit): T {
    val action = rememberUpdatedState(newValue = onClick)

    LaunchedEffect(key1 = Unit) {

        interactions
            .collectLatest { interaction ->
                if (interaction is PressInteraction.Press) action.value()
            }
    }

    return this
}

fun <T> equivalentSnapshotPolicy(isEquivalent: (new: T, previous: T) -> Boolean): SnapshotMutationPolicy<T> =
    object : SnapshotMutationPolicy<T> {
        override fun equivalent(a: T, b: T): Boolean = isEquivalent(a, b)
    }
