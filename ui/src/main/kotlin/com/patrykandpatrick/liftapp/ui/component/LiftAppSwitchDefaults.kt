package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.liftapp.ui.theme.disabled

/**
 * Colors that keep Material's switch in the app's own language.
 *
 * On, it is solid in the accent, as [LiftAppCheckbox] is. Off, it takes the card's surface fill
 * from [LiftAppCardDefaults.cardColors], with the border and thumb using the same color as an
 * unchecked [LiftAppCheckbox].
 */
object LiftAppSwitchDefaults {

    @Composable
    fun colors(
        checkedColor: Color = colorScheme.primary,
        checkedThumbColor: Color = colorScheme.onPrimary,
        uncheckedColor: Color = colorScheme.foregroundVariant,
        uncheckedTrackColor: Color = colorScheme.surface,
    ): SwitchColors =
        SwitchDefaults.colors(
            checkedThumbColor = checkedThumbColor,
            checkedTrackColor = checkedColor,
            checkedBorderColor = checkedColor,
            uncheckedThumbColor = uncheckedColor,
            uncheckedTrackColor = uncheckedTrackColor,
            uncheckedBorderColor = uncheckedColor,
            disabledCheckedThumbColor = checkedThumbColor.disabled,
            disabledCheckedTrackColor = checkedColor.disabled,
            disabledCheckedBorderColor = checkedColor.disabled,
            // The track is a background, so it stays as it is and only what sits on it fades.
            disabledUncheckedThumbColor = uncheckedColor.disabled,
            disabledUncheckedTrackColor = uncheckedTrackColor,
            disabledUncheckedBorderColor = uncheckedColor.disabled,
        )
}
