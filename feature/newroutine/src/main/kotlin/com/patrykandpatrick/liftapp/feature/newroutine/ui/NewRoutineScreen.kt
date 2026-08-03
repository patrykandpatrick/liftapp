package com.patrykandpatrick.liftapp.feature.newroutine.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.feature.newroutine.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens

@Composable
fun NewRoutineScreen(modifier: Modifier = Modifier) {
    val viewModel: NewRoutineViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    state.Unfold(modifier) { routineState ->
        RoutineNameScreen(state = routineState, onAction = viewModel::onAction)
    }
}

@Composable
private fun RoutineNameScreen(
    state: NewRoutineState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAtLeastMediumWidth =
        currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = {
                    Text(
                        stringResource(
                            if (state.isEdit) {
                                R.string.title_rename_routine
                            } else {
                                R.string.title_new_routine
                            }
                        )
                    )
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
                actions = {
                    if (isAtLeastMediumWidth) {
                        PlainLiftAppButton(onClick = { onAction(Action.SaveRoutine) }) {
                            Text(stringResource(R.string.action_save))
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (!isAtLeastMediumWidth) {
                BottomAppBar.Save(onClick = { onAction(Action.SaveRoutine) })
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .fillMaxSize()
                    .padding(top = LocalDimens.current.screen.padding)
        ) {
            LiftAppTextFieldWithSupportingText(
                textFieldState = state.name,
                placeholder = { Text(stringResource(R.string.generic_name)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAction(Action.SaveRoutine) }),
                maxLines = 3,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(horizontal = LocalDimens.current.screen.padding),
            )
        }
    }
}
