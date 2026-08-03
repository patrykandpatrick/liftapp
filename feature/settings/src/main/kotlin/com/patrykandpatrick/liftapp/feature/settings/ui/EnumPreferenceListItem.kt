package com.patrykandpatrick.liftapp.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.patrykandpatrick.liftapp.core.ui.DropdownMenu
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition

@Composable
fun <T : Enum<T>> EnumPreferenceListItem(
    title: String,
    selectedValue: T?,
    values: Array<T>,
    imageVector: ImageVector,
    getValueTitle: @Composable (T) -> String,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    position: LiftAppListItemPosition = LiftAppListItemPosition.Single,
) {
    DropdownMenu(
        selectedItems = listOfNotNull(selectedValue),
        items = values.toList(),
        getItemText = getValueTitle,
        modalTitle = title,
        onClick = onValueChange,
        isMultiSelect = false,
    ) { _, setExpanded ->
        LiftAppListItem(
            title = title,
            imageVector = imageVector,
            description = if (selectedValue != null) getValueTitle(selectedValue) else "",
            modifier = modifier,
            position = position,
            onClick = { setExpanded(true) },
        )
    }
}
