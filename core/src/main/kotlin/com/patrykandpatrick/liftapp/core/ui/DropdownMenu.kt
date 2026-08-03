package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.resource.prettyName
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppCheckbox
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppModalBottomSheet
import com.patrykandpatrick.liftapp.ui.component.LiftAppRadioButton
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.fadingEdges
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.bottomSheetShadow
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlinx.coroutines.launch

@Composable
fun <T> DropdownMenu(
    selectedItems: List<T>,
    items: List<T>,
    getItemText: @Composable (T) -> String,
    modalTitle: String = "",
    onClick: (T) -> Unit,
    onClear: (() -> Unit)? = null,
    isMultiSelect: Boolean = true,
    disabledItems: List<T>? = null,
    onDismissed: (() -> Unit)? = null,
    content: @Composable (expanded: Boolean, setExpanded: (Boolean) -> Unit) -> Unit,
) {
    val (expanded, setExpanded) = rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded,
        setExpanded,
        selectedItems,
        items,
        getItemText,
        modalTitle,
        onClick,
        onClear,
        isMultiSelect,
        disabledItems,
        onDismissed,
        content,
    )
}

@Composable
fun <T> DropdownMenu(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    selectedItems: List<T>,
    items: List<T>,
    getItemText: @Composable (T) -> String,
    modalTitle: String,
    onClick: (T) -> Unit,
    onClear: (() -> Unit)? = null,
    isMultiSelect: Boolean = true,
    disabledItems: List<T>? = null,
    onDismissed: (() -> Unit)? = null,
    content: @Composable (expanded: Boolean, setExpanded: (Boolean) -> Unit) -> Unit,
) {
    content(expanded, setExpanded)

    ListBottomSheet(
        visible = expanded,
        onDismissRequest = {
            setExpanded(false)
            onDismissed?.invoke()
        },
        title = modalTitle,
        items = items,
        selectedItems = selectedItems,
        disabledItems = disabledItems,
        getItemText = getItemText,
        onClick = onClick,
        onClear = onClear,
        isMultiSelect = isMultiSelect,
    )
}

@Composable
private fun <T> ListBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    disabledItems: List<T>?,
    getItemText: @Composable (T) -> String,
    onClick: (T) -> Unit,
    onClear: (() -> Unit)?,
    isMultiSelect: Boolean,
    modifier: Modifier = Modifier,
) {
    if (visible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        LiftAppModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier,
            containerColor = colorScheme.background,
        ) {
            ListBottomSheetContent(
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        onDismissRequest()
                    }
                },
                title = title,
                items = items,
                selectedItems = selectedItems,
                disabledItems = disabledItems,
                getItemText = getItemText,
                onClick = onClick,
                onClear = onClear,
                isMultiSelect = isMultiSelect,
                modifier = Modifier.bottomSheetShadow(),
            )
        }
    }
}

@Composable
private fun <T> ListBottomSheetContent(
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    selectedItems: List<T>,
    disabledItems: List<T>?,
    getItemText: @Composable (T) -> String,
    onClick: (T) -> Unit,
    onClear: (() -> Unit)?,
    isMultiSelect: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.background(colorScheme.background)) {
        CompactTopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                CompactTopAppBarDefaults.IconButton(
                    imageVector = LiftAppIcons.Cross,
                    onClick = onDismissRequest,
                )
            },
            colors = AppBars.noBackgroundColors,
            windowInsets = WindowInsets(),
        )

        if (isMultiSelect) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.title_x_selected, selectedItems.size),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.foreground,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                )

                if (onClear != null) {
                    PlainLiftAppButton(onClick = onClear) {
                        Text(stringResource(R.string.action_clear))
                    }
                }
            }
        }

        val lazyListState = rememberLazyListState()

        LazyColumn(
            state = lazyListState,
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = if (isMultiSelect) 8.dp else 16.dp,
                ),
            modifier =
                Modifier.fadingEdges(verticalEdgeLength = 32.dp, lazyListState = lazyListState)
                    .weight(1f, fill = false),
        ) {
            itemsIndexed(items) { index, item ->
                val enabled = disabledItems?.contains(item) != true
                val selected = selectedItems.contains(item)
                val selectItem = {
                    onClick(item)
                    if (!isMultiSelect) onDismissRequest()
                }

                LiftAppListItem(
                    title = { Text(getItemText(item)) },
                    icon = {
                        if (isMultiSelect) {
                            LiftAppCheckbox(
                                checked = selected,
                                onCheckedChange = null,
                            )
                        } else {
                            LiftAppRadioButton(
                                selected = selected,
                                onCheck = null,
                            )
                        }
                    },
                    position = LiftAppListItemPosition(index, items.size),
                    checked = selected.takeIf { isMultiSelect },
                    selected = selected.takeIf { !isMultiSelect },
                    nextItemSelected = items.getOrNull(index + 1) in selectedItems,
                    onCheckedChange =
                        if (isMultiSelect) {
                            { selectItem() }
                        } else {
                            null
                        },
                    enabled = enabled,
                    contentPadding =
                        PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        ),
                    onClick = selectItem,
                )
            }
        }

        if (isMultiSelect) {
            LiftAppButton(
                onClick = onDismissRequest,
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp,
                        ),
            ) {
                Text(text = stringResource(id = R.string.action_apply))
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun ListBottomSheetContentMultiSelectPreview() {
    LiftAppTheme {
        LiftAppBackground {
            val muscles = Muscle.entries.toList()

            ListBottomSheetContent(
                onDismissRequest = {},
                title = "Primary Muscles",
                items = muscles,
                selectedItems = muscles.take(muscles.size / 3),
                disabledItems = muscles.filterIndexed { index, _ -> index % 2 == 0 },
                getItemText = { it.prettyName },
                onClick = {},
                onClear = {},
                isMultiSelect = true,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun ListBottomSheetContentPreview() {
    LiftAppTheme {
        LiftAppBackground {
            val muscles = Muscle.entries.toList()

            ListBottomSheetContent(
                onDismissRequest = {},
                title = "Primary Muscles",
                items = muscles,
                selectedItems = muscles.take(muscles.size / 3),
                disabledItems = muscles.filterIndexed { index, _ -> index % 2 == 0 },
                getItemText = { it.prettyName },
                onClick = {},
                onClear = null,
                isMultiSelect = false,
            )
        }
    }
}
