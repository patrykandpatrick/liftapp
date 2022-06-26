package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.resource.prettyName
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType

@Composable
fun <T> DropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedItem: T,
    items: Collection<T>,
    getItemText: @Composable (T) -> String,
    label: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    disabledItems: Collection<T>? = null,
) {
    DropdownMenu(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        selectedItems = listOf(selectedItem),
        items = items,
        getItemText = getItemText,
        getItemsText = { collection -> collection.firstOrNull()?.let { getItemText(it) } ?: "" },
        label = label,
        onClick = onClick,
        modifier = modifier,
        multiSelection = false,
        disabledItems = disabledItems,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedItems: Collection<T>,
    items: Collection<T>,
    getItemText: @Composable (T) -> String,
    getItemsText: @Composable (Collection<T>) -> String,
    label: String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    multiSelection: Boolean = true,
    disabledItems: Collection<T>? = null,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = getItemsText(selectedItems),
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {

            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = getItemText(item)) },
                    trailingIcon = {
                        if (selectedItems.contains(item)) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                            )
                        }
                    },
                    onClick = {
                        onClick(item)
                        if (multiSelection.not()) onExpandedChange(false)
                    },
                    enabled = disabledItems?.contains(item)?.not() ?: true,
                )
            }
        }
    }
}

@Composable
private fun PreviewExerciseTypeDropdownMenu(
    expanded: Boolean,
    darkTheme: Boolean,
) {
    LiftAppTheme(darkTheme = darkTheme) {
        Surface {

            val (isExpanded, setExpanded) = remember { mutableStateOf(expanded) }
            val (type, setType) = remember { mutableStateOf(ExerciseType.Cardio) }

            DropdownMenu(
                expanded = isExpanded,
                onExpandedChange = setExpanded,
                selectedItem = type,
                items = remember { ExerciseType.values().toList() },
                getItemText = { it.prettyName },
                label = stringResource(id = R.string.generic_type),
                onClick = setType,
            )
        }
    }
}

@Preview
@Composable
fun PreviewExerciseTypeDropdownMenuCollapsedLight() {
    PreviewExerciseTypeDropdownMenu(expanded = false, darkTheme = false)
}

@Preview
@Composable
fun PreviewExerciseTypeDropdownMenuExpandedLight() {
    PreviewExerciseTypeDropdownMenu(expanded = true, darkTheme = false)
}

@Preview
@Composable
fun PreviewExerciseTypeDropdownMenuCollapsedDark() {
    PreviewExerciseTypeDropdownMenu(expanded = false, darkTheme = true)
}

@Preview
@Composable
fun PreviewExerciseTypeDropdownMenuExpandedDark() {
    PreviewExerciseTypeDropdownMenu(expanded = true, darkTheme = true)
}
