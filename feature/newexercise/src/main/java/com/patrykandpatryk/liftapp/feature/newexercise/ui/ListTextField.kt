package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.DropdownMenu
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType

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

    DropdownMenu(
        expanded = expanded,
        setExpanded = onExpandedChange,
        selectedItems = selectedItems,
        items = items,
        getItemText = getItemText,
        modalTitle = label,
        onClick = onClick,
        onClear = onClear,
        isMultiSelect = isMultiSelect,
        disabledItems = disabledItems,
        onDismissed = { focusManager.clearFocus() },
    ) { expanded, setExpanded ->
        LiftAppTextFieldWithSupportingText(
            modifier =
                modifier
                    .onFocusChanged { focusState -> setExpanded(focusState.isFocused) }
                    .fillMaxWidth(),
            readOnly = true,
            value = getItemsText(selectedItems),
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            errorText = errorText.takeIf { isError }?.let(::AnnotatedString),
        )
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
