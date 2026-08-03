package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.sqrt

private val DiamondSize = 24.dp
private val DiamondBoundsSize = DiamondSize * sqrt(2f)

@Composable
fun SetIndexIcon(
    setIndex: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    label: String = "${setIndex + 1}",
) {
    val shape = RoundedCornerShape(6.dp)
    val color = colorScheme.foregroundVariant
    Box(
        modifier = modifier.size(DiamondBoundsSize),
        contentAlignment = Alignment.Center,
    ) {
        val diamondModifier =
            Modifier.size(DiamondSize)
                .graphicsLayer { rotationZ = 45f }
                .let {
                    if (isCompleted) it.background(color, shape) else it.border(1.dp, color, shape)
                }
        Box(modifier = diamondModifier)

        CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1f)) {
            LiftAppText(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (isCompleted) {
                        colorScheme.surface
                    } else {
                        colorScheme.foreground
                    },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun SetIndexIconPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SetIndexIcon(setIndex = 0, isCompleted = true)
                SetIndexIcon(setIndex = 1, isCompleted = false)
                SetIndexIcon(setIndex = 98, isCompleted = false)
            }
        }
    }
}
