package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextField
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.InfoCard
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.animation.StiffnessForAppearance
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.feature.onerepmax.model.Action
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
    if (
        currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass ==
            WindowWidthSizeClass.COMPACT
    ) {
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
    LiftAppScaffold(
        modifier = modifier,
        topBar = {
            CompactTopAppBar(
                title = { Text(text = stringResource(id = R.string.route_one_rep_max)) },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
            )
        },
    ) { paddingValues ->
        val history = state.history.collectAsStateWithLifecycle().value

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(horizontal = LocalDimens.current.padding.contentHorizontal)
                    .padding(paddingValues = paddingValues),
        ) {
            Calculator(state = state)

            InfoCard(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(top = LocalDimens.current.verticalItemSpacing),
            )

            AnimatedVisibility(
                visible = history.isNotEmpty(),
                enter =
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessForAppearance)) +
                        scaleIn(
                            initialScale = .95f,
                            animationSpec =
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                ),
                        ),
                exit =
                    fadeOut(spring(stiffness = Spring.StiffnessForAppearance)) +
                        scaleOut(
                            targetScale = .95f,
                            animationSpec =
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium,
                                ),
                        ),
            ) {
                History(
                    history = history,
                    removeHistory = state::clearHistory,
                    modifier = Modifier.padding(vertical = LocalDimens.current.verticalItemSpacing),
                )
            }
        }
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
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        val history = state.history.collectAsStateWithLifecycle().value

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(space = LocalDimens.current.padding.itemHorizontal),
            modifier =
                Modifier.padding(
                        horizontal = LocalDimens.current.padding.contentHorizontal,
                        vertical = LocalDimens.current.padding.contentVertical,
                    )
                    .padding(paddingValues = paddingValues),
        ) {
            Calculator(
                state = state,
                modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(LocalDimens.current.verticalItemSpacing),
                modifier = Modifier.fillMaxHeight().weight(1f),
            ) {
                InfoCard(text = stringResource(id = R.string.one_rep_max_description))

                History(history = history, removeHistory = state::clearHistory)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    OutlinedCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            modifier =
                Modifier.padding(
                    start = LocalDimens.current.padding.itemHorizontal,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = LocalDimens.current.verticalItemSpacing,
                ),
        ) {
            ListSectionTitle(
                title = stringResource(R.string.one_rep_max_history_section_title),
                paddingValues = PaddingValues(),
                trailingIcon = {
                    IconButton(onClick = removeHistory) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = null,
                        )
                    }
                },
            )

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(space = LocalDimens.current.verticalItemSpacing),
                reverseLayout = true,
                modifier = Modifier.weight(1f),
            ) {
                items(history, key = { it.id }) { historyEntryModel ->
                    HistoryEntry(
                        historyEntryModel = historyEntryModel,
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryEntry(historyEntryModel: HistoryEntryModel, modifier: Modifier = Modifier) {
    Text(
        text =
            stringResource(
                R.string.one_rep_max_history_entry,
                historyEntryModel.mass,
                historyEntryModel.reps,
                historyEntryModel.oneRepMax,
            ),
        style = MaterialTheme.typography.titleSmall,
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
