package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults.DismissButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.AlertDialogShape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppAlertDialog(
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(),
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        LiftAppCard(
            modifier = modifier.defaultMinSize(minWidth = dimens.dialog.minWidth),
            verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
            contentPadding = PaddingValues(dimens.dialog.paddingLarge),
            shape = AlertDialogShape,
        ) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) { icon?.invoke() }

            Box(
                modifier =
                    Modifier.align(
                        if (icon != null) Alignment.CenterHorizontally else Alignment.Start
                    )
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides colorScheme.onSurface,
                    LocalTextStyle provides MaterialTheme.typography.headlineSmall,
                ) {
                    title()
                }
            }

            text()

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
                modifier =
                    Modifier.align(Alignment.End).padding(top = dimens.padding.contentVerticalSmall),
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}

object LiftAppAlertDialogDefaults {

    @Composable
    fun DismissButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) =
        PlainLiftAppButton(onClick = onClick, showDivider = false, modifier = modifier) {
            Text(text)
        }
}

@Composable
@LightAndDarkThemePreview
private fun LiftAppAlertDialogPreview() {
    LiftAppTheme {
        LiftAppAlertDialog(
            title = { Text("Title") },
            text = { Text("This is a sample alert dialog text.") },
            onDismissRequest = {},
            confirmButton = { PlainLiftAppButton(onClick = {}) { Text("Confirm") } },
            dismissButton = { DismissButton(onClick = {}, text = "Dismiss") },
            icon = { Icon(LiftAppIcons.Delete, null) },
        )
    }
}
