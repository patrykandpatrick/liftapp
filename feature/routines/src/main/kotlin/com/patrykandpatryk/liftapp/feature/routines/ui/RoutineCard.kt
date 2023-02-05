package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.core.extension.forEachIndexedExtended
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun RoutineCard(
    title: String,
    exercises: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    val cardPadding = LocalDimens.current.card
    val shape = MaterialTheme.shapes.medium

    OutlinedCard(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
    ) {

        Column(
            Modifier.padding(
                horizontal = cardPadding.contentPaddingHorizontal,
                vertical = cardPadding.contentPaddingVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(padding.contentVerticalSmall),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                text = buildAnnotatedString {
                    pushStyle(ParagraphStyle(lineHeight = 24.sp))
                    exercises.forEachIndexedExtended { _, _, isLast, value ->
                        append("• $value")
                        if (!isLast) append("\n")
                    }
                    pop()
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewRoutineCard() {
    LiftAppTheme {
        RoutineCard(
            title = "This is a title",
            exercises = listOf("Bench Press", "Deadlift", "Sit-Ups"),
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
