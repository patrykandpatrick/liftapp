package com.patrykandpatryk.liftapp.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.patrykandpatryk.liftapp.core.ui.DialogContent
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.dialog.DialogButtons

@Composable
fun <T : Enum<T>> EnumPreferenceListItem(
    title: String,
    selectedValue: T?,
    values: Array<T>,
    iconPainter: Painter,
    getValueTitle: @Composable (T) -> String,
    onValueChange: (T) -> Unit,
) {
    var dialogVisible by remember { mutableStateOf(value = false) }

    ListItem(
        title = title,
        iconPainter = iconPainter,
        description = if (selectedValue != null) getValueTitle(selectedValue) else "",
        onClick = { dialogVisible = true },
    )

    if (dialogVisible) {
        Dialog(onDismissRequest = { dialogVisible = false }) {
            DialogContent(
                title = title,
                actions = {
                    DialogButtons(onNegativeButtonClick = { dialogVisible = false })
                },
            ) {
                values.forEach { value ->

                    ValueRow(
                        title = getValueTitle(value),
                        selected = value == selectedValue,
                        onClick = {
                            onValueChange(value)
                            dialogVisible = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ValueRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )

        Text(text = title)
    }
}
