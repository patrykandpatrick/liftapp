package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens

@Composable
internal fun WorkoutInputBottomSheetContent(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        verticalArrangement = verticalArrangement,
        modifier =
            Modifier.fillMaxWidth()
                .then(modifier)
                .imePadding()
                .padding(
                    start = dimens.screen.padding,
                    top = 8.dp,
                    end = dimens.screen.padding,
                    bottom = 16.dp,
                ),
        content = content,
    )
}
