package com.patrykandpatryk.liftapp.feature.newroutine.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FabPosition
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.collectInComposable
import com.patrykandpatryk.liftapp.core.extension.getMessageTextOrNull
import com.patrykandpatryk.liftapp.core.extension.isLandscape
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.state.equivalentSnapshotPolicy
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Event
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Intent
import com.patrykandpatryk.liftapp.feature.newroutine.domain.ScreenState

@Composable
fun NewRoutine(
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: NewRoutineViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    val errorMessage by remember {
        derivedStateOf(
            equivalentSnapshotPolicy { new, previous -> if (state.showErrors) new == previous else true },
        ) { state.name.getMessageTextOrNull() ?: "" }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    NewRoutine(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        popBackStack = popBackStack,
        nameErrorText = errorMessage,
    )

    viewModel.events.collectInComposable { event ->
        when (event) {
            Event.EntrySaved -> popBackStack()
            Event.RoutineNotFound -> popBackStack()
        }
    }
}

@Composable
private fun NewRoutine(
    modifier: Modifier = Modifier,
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    snackbarHostState: SnackbarHostState,
    popBackStack: () -> Unit,
    nameErrorText: String,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .thenIf(isLandscape) { navigationBarsPadding() }
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.title_new_routine),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = popBackStack,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding(),
                text = stringResource(id = R.string.action_save),
                icon = painterResource(id = R.drawable.ic_save),
                onClick = { onIntent(Intent.Save) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
        ) {
            Content(
                state = state,
                onIntent = onIntent,
                nameErrorText = nameErrorText,
            )
        }
    }
}

@Composable
private fun ColumnScope.Content(
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    nameErrorText: String,
) {

    OutlinedTextField(
        value = state.name.value,
        onValueChange = { onIntent(Intent.UpdateName(it)) },
        label = { Text(text = stringResource(id = R.string.generic_name)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onIntent(Intent.Save) }),
        maxLines = LocalDimens.current.input.nameMaxLines,
    )

    SupportingText(
        text = nameErrorText,
        visible = state.showErrors,
        isError = true,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.TABLET)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.TABLET)
@Composable
fun NewRoutinePreview() {
    LiftAppTheme {
        NewRoutine(
            state = ScreenState.Insert(name = "Name".toValid()),
            snackbarHostState = SnackbarHostState(),
            onIntent = {},
            popBackStack = {},
            nameErrorText = "",
        )
    }
}
