package com.patrykandpatrick.liftapp.feature.onerepmax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.stringResourceId
import com.patrykandpatrick.liftapp.core.isCompactWidth
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewResource
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.InfoCard
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.onerepmax.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppDestructiveActionDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextField
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

@Composable
fun OneRepMaxScreen(modifier: Modifier = Modifier) {
    val viewModel = hiltViewModel<OneRepMaxViewModel>()
    OneRepMaxScreen(state = viewModel.state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun OneRepMaxScreen(
    state: OneRepMaxState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isCompactWidth) {
        OneRepMaxScreenCompact(state = state, onAction = onAction, modifier = modifier)
    } else {
        OneRepMaxScreenLarge(state = state, onAction = onAction, modifier = modifier)
    }
}

@Composable
private fun OneRepMaxScreenCompact(
    state: OneRepMaxState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    var hasObservedHistory by remember { mutableStateOf(false) }
    var showClearHistoryDialog by rememberSaveable { mutableStateOf(false) }

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = { Text(text = stringResource(id = R.string.route_one_rep_max)) },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
            )
        },
    ) { paddingValues ->
        val history = state.history.collectAsStateWithLifecycle().value
        LaunchedEffect(history.firstOrNull()?.id) {
            if (hasObservedHistory && history.isNotEmpty()) listState.animateScrollToItem(2)
            hasObservedHistory = true
        }

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding =
                PaddingValues(
                    start = LocalDimens.current.screen.padding,
                    top = paddingValues.calculateTopPadding(),
                    end = LocalDimens.current.screen.padding,
                    bottom =
                        paddingValues.calculateBottomPadding() + LocalDimens.current.screen.padding,
                ),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "calculator") { Calculator(state = state) }

            item(key = "description") {
                InfoCard(
                    text = stringResource(id = R.string.one_rep_max_description),
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            if (history.isNotEmpty()) {
                item(key = "history-header") {
                    HistoryHeader(onClearClick = { showClearHistoryDialog = true })
                }

                itemsIndexed(history, key = { _, entry -> entry.id }) { index, entry ->
                    HistoryEntry(
                        historyEntryModel = entry,
                        position = LiftAppListItemPosition(index, history.size),
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }

    if (showClearHistoryDialog) {
        ClearHistoryDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            onConfirm = {
                showClearHistoryDialog = false
                state.clearHistory()
            },
        )
    }
}

@Composable
fun OneRepMaxScreenLarge(
    state: OneRepMaxState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.route_one_rep_max)) },
                navigationIcon = {
                    IconButton(onClick = { onAction(Action.PopBackStack) }) {
                        Icon(
                            imageVector = LiftAppIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        val history = state.history.collectAsStateWithLifecycle().value

        LazyColumn(
            contentPadding =
                PaddingValues(
                    start = LocalDimens.current.screen.padding,
                    top = paddingValues.calculateTopPadding() + LocalDimens.current.screen.padding,
                    end = LocalDimens.current.screen.padding,
                    bottom =
                        paddingValues.calculateBottomPadding() + LocalDimens.current.screen.padding,
                ),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "content") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Calculator(state = state, modifier = Modifier.weight(1f))

                    Column(modifier = Modifier.weight(1f)) {
                        InfoCard(text = stringResource(id = R.string.one_rep_max_description))

                        History(history = history, removeHistory = state::clearHistory)
                    }
                }
            }
        }
    }
}

@Composable
private fun Calculator(state: OneRepMaxState, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val massUnit = state.massUnit.collectAsStateWithLifecycle().value

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = state.oneRepMax.collectAsStateWithLifecycle().value,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.one_rep_max),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.foregroundVariant,
            modifier = Modifier.padding(top = 4.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
            modifier = Modifier.padding(top = 32.dp),
        ) {
            TextField(
                value = state.mass.value,
                onValueChange = state::updateMass,
                keyboardOptions =
                    KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Decimal,
                    ),
                label = stringResource(id = R.string.mass),
                trailingIcon = { Text(text = stringResource(id = massUnit.stringResourceId)) },
                modifier = Modifier,
            )

            TextField(
                value = state.reps.value,
                onValueChange = state::updateReps,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                        }
                    ),
                label = stringResource(id = R.string.reps),
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun RowScope.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    LiftAppTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        label = { Text(text = label) },
        trailingIcon = trailingIcon,
        modifier = modifier.weight(weight = 1f),
    )
}

@Composable
private fun History(
    history: ImmutableList<HistoryEntryModel>,
    removeHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showClearHistoryDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        HistoryHeader(onClearClick = { showClearHistoryDialog = true })

        history.forEachIndexed { index, historyEntryModel ->
            HistoryEntry(
                historyEntryModel = historyEntryModel,
                position = LiftAppListItemPosition(index, history.size),
            )
        }
    }

    if (showClearHistoryDialog) {
        ClearHistoryDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            onConfirm = {
                showClearHistoryDialog = false
                removeHistory()
            },
        )
    }
}

@Composable
private fun HistoryHeader(onClearClick: () -> Unit, modifier: Modifier = Modifier) {
    val buttonPadding = LiftAppButtonDefaults.plainContentPadding
    val layoutDirection = LocalLayoutDirection.current
    val horizontalInset = LocalDimens.current.screen.padding

    ListSectionTitle(
        title = stringResource(R.string.one_rep_max_history_section_title),
        inset = ListSectionTitleDefaults.Inset.Screen,
        spacing = ListSectionTitleDefaults.Spacing.Section,
        endPadding =
            (horizontalInset - buttonPadding.calculateEndPadding(layoutDirection)).coerceAtLeast(
                0.dp
            ),
        trailingIcon = {
            PlainLiftAppButton(onClick = onClearClick) {
                Text(stringResource(R.string.action_clear))
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ClearHistoryDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    LiftAppDestructiveActionDialog(
        title = stringResource(R.string.one_rep_max_clear_history_title),
        text = stringResource(R.string.one_rep_max_clear_history_message),
        confirmText = stringResource(R.string.action_clear),
        dismissText = stringResource(android.R.string.cancel),
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
    )
}

@Composable
private fun HistoryEntry(
    historyEntryModel: HistoryEntryModel,
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    LiftAppListItem(
        title = {
            Text(
                text =
                    stringResource(
                        R.string.one_rep_max_history_entry,
                        historyEntryModel.mass,
                        historyEntryModel.reps,
                        historyEntryModel.oneRepMax,
                    ),
                style = MaterialTheme.typography.titleSmall,
            )
        },
        position = position,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun OneRepMaxPreview(history: List<HistoryEntryModel>) {
    LiftAppTheme {
        val formatter = PreviewResource.formatter()
        val savedStateHandle = remember {
            SavedStateHandle().apply { set(OneRepMaxState.HISTORY_KEY, history) }
        }
        OneRepMaxScreen(
            state =
                OneRepMaxState(
                    coroutineScope = rememberCoroutineScope { Dispatchers.Unconfined },
                    savedStateHandle = savedStateHandle,
                    getMassUnit = { flowOf(MassUnit.Kilograms) },
                    formatWeight = formatter::formatWeight,
                ),
            onAction = {},
        )
    }
}

@MultiDevicePreview
@Composable
private fun OneRepMaxPreview_NoHistory() {
    OneRepMaxPreview(emptyList())
}

@MultiDevicePreview
@Composable
private fun OneRepMaxPreview_WithHistory() {
    OneRepMaxPreview(
        listOf(HistoryEntryModel(5, "100 kg", "116.67 kg"), HistoryEntryModel(8, "90 kg", "114 kg"))
    )
}
