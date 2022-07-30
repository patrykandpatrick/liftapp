package com.patrykandpatryk.liftapp.core.ui.input

import android.content.res.Configuration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.VerticalDivider
import com.patrykandpatryk.liftapp.core.ui.button.IconButton
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    onPlusClick: (isLong: Boolean) -> Unit,
    onMinusClick: (isLong: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    prefix: String? = null,
    suffix: String? = null,
) {
    OutlinedTextField(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = if (hint != null) {
            {
                Text(text = hint)
            }
        } else null,
        leadingIcon = {

            Row(
                modifier = Modifier.padding(end = LocalDimens.current.padding.contentHorizontalSmall),
            ) {

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onLongClick = { onMinusClick(true) },
                    onClick = { onMinusClick(false) },
                    repeatLongClicks = true,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_minus),
                        contentDescription = stringResource(id = R.string.action_increase),
                    )
                }

                VerticalDivider(
                    modifier = Modifier.padding(vertical = LocalDimens.current.padding.itemVertical),
                )

                if (prefix != null) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = LocalDimens.current.padding.contentHorizontal),
                        text = prefix,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        trailingIcon = {

            Row(
                modifier = Modifier.padding(start = LocalDimens.current.padding.contentHorizontalSmall),
            ) {

                if (suffix != null) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = LocalDimens.current.padding.contentHorizontal),
                        text = suffix,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                VerticalDivider(
                    modifier = Modifier.padding(vertical = LocalDimens.current.padding.itemVertical),
                )

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onLongClick = { onPlusClick(true) },
                    onClick = { onPlusClick(false) },
                    repeatLongClicks = true,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = stringResource(id = R.string.action_decrease),
                    )
                }
            }
        },
        singleLine = true,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNumberInput() {
    LiftAppTheme {
        Surface {
            NumberInput(
                modifier = Modifier.padding(16.dp),
                value = "40",
                hint = "Weight",
                onValueChange = {},
                onPlusClick = {},
                onMinusClick = {},
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNumberInputWithPrefixAndSuffix() {
    LiftAppTheme {
        Surface {
            NumberInput(
                modifier = Modifier.padding(16.dp),
                value = "40",
                hint = "Weight",
                onValueChange = {},
                onPlusClick = {},
                onMinusClick = {},
                suffix = "kg",
                prefix = "Weight",
            )
        }
    }
}
