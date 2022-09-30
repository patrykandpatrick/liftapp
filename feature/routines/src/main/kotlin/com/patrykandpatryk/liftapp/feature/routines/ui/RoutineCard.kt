package com.patrykandpatryk.liftapp.feature.routines.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.vico.core.extension.forEachIndexedExtended

@Composable
fun RoutineCard(
    modifier: Modifier = Modifier,
    title: String,
    exercises: List<String>,
) {
    val padding = LocalDimens.current.padding
    val cardPadding = LocalDimens.current.card

    OutlinedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
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
                style = MaterialTheme.typography.titleLarge,
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
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRoutineCard() {
    LiftAppTheme {
        RoutineCard(
            modifier = Modifier.padding(8.dp),
            title = "This is a title",
            exercises = listOf("Bench Press", "Deadlift", "Sit-Ups"),
        )
    }
}
