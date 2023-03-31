package com.patrykandpatryk.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.indicator.RoundBackgroundWithBadge
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

private const val PREVIEW_ITEM_COUNT = 3

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetEntry(
    modifier: Modifier = Modifier,
    setIndex: Int,
    completed: Boolean,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.padding.itemHorizontal),
    ) {

        RoundBackgroundWithBadge(
            content = {
                Text(
                    text = setIndex.toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center),
                )
            },
            outlined = completed,
            showBadge = completed,
            modifier = Modifier
                .padding(top = 14.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.padding.itemVertical),
        ) {

            FlowRow(
                maxItemsInEachRow = 2,
                modifier = Modifier,
            ) {

                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    NumberInput(
                        value = "",
                        onValueChange = {},
                        onPlusClick = {},
                        onMinusClick = {},
                        hint = stringResource(id = R.string.exercise_type_weight),
                        modifier = Modifier.weight(1f),
                    )
                }

                NumberInput(
                    value = "",
                    onValueChange = {},
                    onPlusClick = {},
                    onMinusClick = {},
                    hint = stringResource(id = R.string.reps),
                    modifier = Modifier,
                )
            }

            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.button.iconPadding))

                Text(
                    text = "90 kg x 5",
                    maxLines = 1,
                )
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun SetEntryPreview() {
    LiftAppTheme {
        Surface {
            Column {
                repeat(PREVIEW_ITEM_COUNT) { index ->
                    SetEntry(
                        setIndex = index + 1,
                        completed = index == 0,
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.dimens.padding.contentHorizontal,
                            vertical = MaterialTheme.dimens.padding.itemVertical,
                        )
                    )
                }
            }
        }
    }
}
