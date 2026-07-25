package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.animateContainerColorsAsState
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.feature.dashboard.model.DashboardState
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun DaysOfWeek(
    modifier: Modifier = Modifier,
    dateItems: List<DashboardState.DayItem>,
    onClick: (LocalDate) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
    ) {
        dateItems.forEach { dateItem ->
            DayOfWeek(
                date = dateItem.date,
                selected = dateItem.isSelected,
                isToday = dateItem.isToday,
                onClick = { onClick(dateItem.date) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@Composable
internal fun DayOfWeek(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick = onClick,
        colors =
            animateContainerColorsAsState(
                    if (selected) LiftAppCardDefaults.tonalCardColors
                    else LiftAppCardDefaults.outlinedColors
                )
                .value,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = dimens.padding.itemVerticalMedium),
        shape = CircleShape,
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
            textAlign = TextAlign.Center,
            color = colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            Text(
                text = date.dayOfMonth.toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            SinHorizontalDivider(
                sinHeight = 4.dp,
                sinPeriodLength = 1.5.dp,
                color = colorScheme.onSurfaceVariant,
                thickness = dimens.button.underlineWidth,
                modifier = Modifier.graphicsLayer { alpha = if (isToday) 1f else 0f },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun DaysOfWeekPreview() {
    LiftAppTheme {
        LiftAppBackground {
            DaysOfWeek(
                dateItems = DashboardViewModel.getWeekDays(LocalDate.now()),
                onClick = {},
                modifier = Modifier.padding(horizontal = dimens.padding.contentHorizontal),
            )
        }
    }
}
