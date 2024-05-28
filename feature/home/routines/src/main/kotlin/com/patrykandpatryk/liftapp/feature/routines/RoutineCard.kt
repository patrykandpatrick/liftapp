package com.patrykandpatryk.liftapp.feature.routines

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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.component.shape.composeShape
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatrick.vico.core.component.shape.cornered.CutCornerTreatment
import com.patrykandpatrick.vico.core.component.shape.cornered.RoundedCornerTreatment
import com.patrykandpatrick.vico.core.extension.forEachIndexedExtended
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

val RoutineCardShape: Shape =
    CorneredShape(
        topLeft = Corner.Absolute(16f, CutCornerTreatment),
        topRight = Corner.Absolute(12f, RoundedCornerTreatment),
        bottomLeft = Corner.Absolute(12f, RoundedCornerTreatment),
        bottomRight = Corner.Absolute(12f, RoundedCornerTreatment),
    ).composeShape()

@Composable
fun RoutineCard(
    title: String,
    exercises: ImmutableList<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    val cardPadding = LocalDimens.current.card

    OutlinedCard(
        modifier = modifier
            .clip(RoutineCardShape)
            .clickable(onClick = onClick),
        shape = RoutineCardShape,
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
