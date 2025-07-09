package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import kotlinx.coroutines.launch

@Composable
fun <T> ListTextField(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedItem: T,
    items: List<T>,
    getItemText: @Composable (T) -> String,
    label: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    disabledItems: List<T>? = null,
    hasError: Boolean = false,
    errorText: String? = null,
) {
    ListTextField(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        selectedItems = listOf(selectedItem),
        items = items,
        getItemText = getItemText,
        getItemsText = { collection ->
            collection.firstOrNull()?.let { getItemText(it) }.orEmpty()
        },
        label = label,
        onClick = onClick,
        modifier = modifier,
        isMultiSelect = false,
        disabledItems = disabledItems,
        isError = hasError,
        errorText = errorText,
    )
}

@Composable
fun <T> ListTextField(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedItems: List<T>,
    items: List<T>,
    getItemText: @Composable (T) -> String,
    getItemsText: @Composable (List<T>) -> String,
    label: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    onClear: (() -> Unit)? = null,
    isMultiSelect: Boolean = true,
    disabledItems: List<T>? = null,
    isError: Boolean = false,
    errorText: String? = null,
) {
    val focusManager = LocalFocusManager.current

    LiftAppTextFieldWithSupportingText(
        modifier =
            modifier
                .onFocusChanged { focusState -> onExpandedChange(focusState.isFocused) }
                .fillMaxWidth(),
        readOnly = true,
        value = getItemsText(selectedItems),
        onValueChange = {},
        label = { Text(text = label) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        errorText = errorText.takeIf { isError }?.let(::AnnotatedString),
    )

    ListBottomSheet(
        visible = expanded,
        onDismissRequest = {
            onExpandedChange(false)
            focusManager.clearFocus(force = true)
        },
        title = label,
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
            title = { Text(text = title) },
            navigationIcon = {
                CompactTopAppBarDefaults.IconButton(
                    imageVector = LiftAppIcons.Cross,
                    onClick = onDismissRequest,
                )
            },
            modifier = Modifier.padding(vertical = dimens.padding.itemVertical),
        )

        if (isMultiSelect) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = dimens.padding.contentHorizontal),
            ) {
                Text(
                    text = stringResource(R.string.title_x_selected, selectedItems.size),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f).padding(start = dimens.chip.horizontalPadding),
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
private fun ListTextFieldPreview() {
    LiftAppTheme {
        Surface {
            ListTextField(
                expanded = false,
                onExpandedChange = {},
                selectedItem = ExerciseType.Cardio,
                items = ExerciseType.entries,
                getItemText = { it.prettyName },
                label = stringResource(id = R.string.generic_type),
                onClick = {},
            )
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
