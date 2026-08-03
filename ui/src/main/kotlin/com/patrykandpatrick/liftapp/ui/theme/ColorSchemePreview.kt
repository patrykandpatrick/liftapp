package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview

@LightAndDarkThemePreview
@Composable
private fun ColorSchemePreview() {
    LiftAppTheme { ColorSchemePreviewContent(colorScheme) }
}

@Composable
private fun ColorSchemePreviewContent(scheme: ColorScheme) {
    val colors =
        listOf(
            "primary" to scheme.primary,
            "primaryDisabled" to scheme.primaryDisabled,
            "onPrimary" to scheme.onPrimary,
            "onPrimaryOutline" to scheme.onPrimaryOutline,
            "onPrimaryDisabled" to scheme.onPrimaryDisabled,
            "secondary" to scheme.secondary,
            "secondaryDisabled" to scheme.secondaryDisabled,
            "onSecondary" to scheme.onSecondary,
            "onSecondaryDisabled" to scheme.onSecondaryDisabled,
            "background" to scheme.background,
            "surface" to scheme.surface,
            "foreground" to scheme.foreground,
            "foregroundVariant" to scheme.foregroundVariant,
            "outline" to scheme.outline,
            "error" to scheme.error,
            "onError" to scheme.onError,
            "green" to scheme.green,
            "yellow" to scheme.yellow,
            "orange" to scheme.orange,
            "red" to scheme.red,
            "bottomSheetScrim" to scheme.bottomSheetScrim,
        )

    LiftAppBackground(color = scheme.background) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            colors.forEach { (name, color) ->
                ColorSwatch(
                    name = name,
                    color = color,
                    labelColor = scheme.foreground,
                    borderColor = scheme.outline,
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(name: String, color: Color, labelColor: Color, borderColor: Color) {
    val shape = RoundedCornerShape(8.dp)

    Column(modifier = Modifier.width(112.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(48.dp)
                    .clip(shape)
                    .background(color)
                    .border(width = 1.dp, color = borderColor, shape = shape)
        )
        Text(
            text = name,
            color = labelColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
