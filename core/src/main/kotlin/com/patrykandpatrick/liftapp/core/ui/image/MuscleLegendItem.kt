package com.patrykandpatrick.liftapp.core.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.model.MuscleModel
import com.patrykandpatrick.liftapp.core.ui.resource.color
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun MuscleLegendItem(muscleModel: MuscleModel, modifier: Modifier = Modifier) {
    val muscleDimens = LocalDimens.current.muscle
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(muscleDimens.tileSize)
                    .background(
                        color = muscleModel.type.color,
                        shape = RoundedCornerShape(muscleDimens.tileCornerSize),
                    )
        )
        Column {
            Text(
                text = stringResource(id = muscleModel.nameRes),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(id = muscleModel.type.nameRes),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.foregroundVariant,
            )
        }
    }
}
