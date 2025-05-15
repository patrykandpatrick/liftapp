package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.RoutineCardShape
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RoutineCard(
    title: String,
    exercises: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    val cardPadding = LocalDimens.current.card

    OutlinedCard(
        modifier = modifier.clip(RoutineCardShape).clickable(onClick = onClick),
        shape = RoutineCardShape,
    ) {
        Column(
            Modifier.padding(
                horizontal = cardPadding.contentPaddingHorizontal,
                vertical = cardPadding.contentPaddingVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(padding.contentVerticalSmall),
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)

            Text(
                text =
                    buildString {
                        exercises.forEach { value ->
                            if (isNotEmpty()) append("\n")
                            append("• $value")
                        }
                    },
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewRoutineCard() {
    LiftAppTheme {
        Surface {
            RoutineCard(
                title = "This is a title",
                exercises = persistentListOf("Bench Press", "Deadlift", "Sit-Ups"),
                onClick = {},
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
