package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun Info(
    text: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    var size by remember { mutableStateOf(IntSize.Zero) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * .1f, size.height * -1f),
                    radius = (size.width * .8f).coerceAtLeast(1f),
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    center = Offset(size.width * .1f, size.height * -1f),
                    radius = (size.width * .5f).coerceAtLeast(1f),
                ),
                shape = shape,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
            .onGloballyPositioned { layoutCoordinates -> size = layoutCoordinates.size },
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@LightAndDarkThemePreview
fun InfoPreview() {
    LiftAppTheme {
        Surface {
            Info(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}