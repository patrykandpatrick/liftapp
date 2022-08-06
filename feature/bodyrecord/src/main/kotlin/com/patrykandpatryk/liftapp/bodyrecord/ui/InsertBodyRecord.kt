@file:OptIn(ExperimentalMaterial3Api::class)

package com.patrykandpatryk.liftapp.bodyrecord.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.getMessageTextOrNull
import com.patrykandpatryk.liftapp.core.ui.DialogTopBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants.Input.INCREMENT_LONG
import com.patrykandpatryk.liftapp.domain.Constants.Input.INCREMENT_SHORT
import com.patrykandpatryk.liftapp.domain.validation.Validatable

@Composable
fun InsertBodyRecord(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: InsertBodyRecordViewModel = hiltViewModel()
    val bodyModel by viewModel.bodyModel.collectAsState()

    BackHandler(enabled = true, onBack = onCloseClick)

    InsertBodyRecord(
        bodyModel = bodyModel,
        actionHandler = viewModel,
        onCloseClick = onCloseClick,
        modifier = modifier,
    )
}

@Composable
private fun InsertBodyRecord(
    bodyModel: BodyModel,
    actionHandler: BodyActionHandler,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(vertical = dimens.padding.contentVertical),
    ) {
        DialogTopBar(
            title = bodyModel.name,
            onCloseClick = onCloseClick,
        )

        Column(
            modifier = modifier
                .navigationBarsPadding()
                .padding(horizontal = dimens.padding.contentHorizontal)
                .padding(top = dimens.padding.contentVertical),
            verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
        ) {

            bodyModel.values.forEachIndexed { index, validatable ->

                NumberInput(
                    bodyModel = bodyModel,
                    index = index,
                    validatable = validatable,
                    actionHandler = actionHandler,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = dimens.padding.itemHorizontal),
            ) {

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = "",
                    onValueChange = {},
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.picker_date)) },
                )

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = "",
                    onValueChange = {},
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.picker_time)) },
                )
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { actionHandler(BodyAction.Save) },
            ) {

                Text(
                    text = stringResource(id = R.string.action_save),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun NumberInput(
    bodyModel: BodyModel,
    index: Int,
    validatable: Validatable<String>,
    actionHandler: BodyActionHandler,
    modifier: Modifier = Modifier,
) {
    NumberInput(
        modifier = modifier,
        value = validatable.value,
        onValueChange = { value ->
            actionHandler(BodyAction.SetValue(index = index, value = value))
        },
        hint = stringResource(id = R.string.value),
        onMinusClick = { long ->
            actionHandler(
                BodyAction.IncrementValue(
                    index = index,
                    incrementBy = -getIncrement(long),
                ),
            )
        },
        onPlusClick = { long ->
            actionHandler(
                BodyAction.IncrementValue(
                    index = index,
                    incrementBy = getIncrement(long),
                ),
            )
        },
        keyboardActions = KeyboardActions(
            onDone = {
                actionHandler(BodyAction.Save)
            },
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (index == bodyModel.values.lastIndex) ImeAction.Done else ImeAction.Next,
        ),
        isError = bodyModel.showErrors,
        errorMessage = validatable.getMessageTextOrNull(),
    )
}

private fun getIncrement(long: Boolean) =
    if (long) INCREMENT_LONG else INCREMENT_SHORT

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewInsertBodyRecord() {
    LiftAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                shape = BottomSheetShape,
                shadowElevation = 8.dp,
            ) {
                InsertBodyRecord(
                    bodyModel = BodyModel.Insert(
                        name = "Weight",
                        values = List(size = 1) { Validatable.Valid("65") },
                    ),
                    actionHandler = {},
                    onCloseClick = {},
                )
            }
        }
    }
}
