package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
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

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier.safeDrawingPadding(),
            dragHandle = null,
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
    Column(modifier = modifier.background(colorScheme.surface)) {
        CompactTopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                CompactTopAppBarDefaults.IconButton(
                    imageVector = LiftAppIcons.Cross,
                    onClick = onDismissRequest,
                )
            },
            modifier =
                Modifier.padding(
                    top = dimens.padding.itemVerticalSmall,
                    bottom = dimens.padding.itemVertical,
                ),
            windowInsets = WindowInsets(),
        )

        if (isMultiSelect) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimens.padding.contentHorizontal),
            ) {
                Text(
                    text = stringResource(R.string.title_x_selected, selectedItems.size),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface,
                    modifier = Modifier.weight(1f).padding(start = dimens.chip.endPadding),
                )

                if (onClear != null) {
                    PlainLiftAppButton(onClick = onClear) {
                        Text(stringResource(R.string.action_clear))
                    }
                }
            }
        }

        LiftAppChipRow(
            modifier =
                Modifier.padding(
                    horizontal = dimens.padding.contentHorizontal,
                    vertical = dimens.padding.itemVertical,
                )
        ) {
            items.forEach { item ->
                LiftAppFilterChip(
                    selected = selectedItems.contains(item),
                    enabled = disabledItems?.contains(item) != true,
                    label = { Text(text = getItemText(item)) },
                    onClick = { onClick(item) },
                )
            }
        }

        LiftAppHorizontalDivider(modifier = Modifier.padding(top = dimens.padding.itemVertical))

        LiftAppButton(
            onClick = onDismissRequest,
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.itemVertical,
                    ),
        ) {
            Text(text = stringResource(id = R.string.action_apply))
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
