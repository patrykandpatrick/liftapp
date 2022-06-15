package com.patrykandpatryk.liftapp.core.logging

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun CollectSnackbarMessages(
    messages: Flow<UiMessage>,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(key1 = Unit) {
        messages
            .collect { uiMessage ->
                when (uiMessage) {
                    is UiMessage.SnackbarText ->
                        snackbarHostState.showSnackbar(uiMessage.message)
                }
            }
    }
}
