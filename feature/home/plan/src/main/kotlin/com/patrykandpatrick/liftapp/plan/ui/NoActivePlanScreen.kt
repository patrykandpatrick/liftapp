package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Open
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.BottomSheetContent
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import kotlinx.coroutines.launch

@Composable
internal fun NoActivePlanScreen(onAction: (Action) -> Unit) {
    val padding = LocalDimens.current.padding
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(padding.itemVerticalSmall, Alignment.CenterVertically),
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = padding.contentHorizontal, vertical = padding.contentVertical),
    ) {
        Text(
            text = stringResource(R.string.plan_no_active_plan),
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "\uD83D\uDCC2",
            fontSize = 88.sp,
            modifier = Modifier.padding(vertical = padding.itemVertical),
        )

        ChooseExistingButton(onAction)

        CreateNewButton(onAction)
    }
}

@Composable
private fun ChooseExistingButton(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    LiftAppButton(onClick = { onAction(Action.ChooseExistingPlan) }, modifier = modifier) {
        Icon(
            imageVector = LiftAppIcons.Open,
            contentDescription = null,
            modifier = Modifier.padding(end = LocalDimens.current.padding.itemHorizontalSmall),
        )

        Text(stringResource(R.string.plan_no_active_plan_choose_existing_cta))
    }
}

@Composable
private fun CreateNewButton(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    LiftAppButton(
        onClick = { onAction(Action.CreateNewPlan) },
        modifier = modifier,
        colors = LiftAppButtonDefaults.outlinedButtonColors,
    ) {
        Icon(
            imageVector = LiftAppIcons.Plus,
            contentDescription = null,
            modifier = Modifier.padding(end = LocalDimens.current.padding.itemHorizontalSmall),
        )
        Text(stringResource(R.string.plan_no_active_plan_create_cta))
    }
}

@Composable
internal fun EditBottomSheet(onDismissRequest: () -> Unit, onAction: (Action) -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = colorScheme.surface,
    ) {
        val coroutineScope = rememberCoroutineScope()

        EditBottomSheetContent(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    onDismissRequest()
                }
            },
            onAction = { action ->
                coroutineScope.launch {
                    sheetState.hide()
                    onDismissRequest()
                    onAction(action)
                }
            },
        )
    }
}

@Composable
private fun EditBottomSheetContent(
    onDismissRequest: () -> Unit,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = dimens.padding
    BottomSheetContent(
        topContent = {
            CompactTopAppBar(
                title = { Text(text = stringResource(R.string.training_plan_change_title)) },
                navigationIcon = {
                    LiftAppIconButton(onClick = onDismissRequest) {
                        Icon(LiftAppIcons.Cross, stringResource(R.string.action_close))
                    }
                },
            )
        },
        bottomContent = {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(padding.itemVerticalSmall, Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = padding.itemVertical),
            ) {
                ChooseExistingButton(
                    onAction,
                    Modifier.fillMaxWidth().padding(horizontal = padding.contentHorizontal),
                )

                CreateNewButton(
                    onAction,
                    Modifier.fillMaxWidth().padding(horizontal = padding.contentHorizontal),
                )
            }
        },
        modifier = modifier.padding(vertical = padding.itemVertical),
    )
}

@Composable
@MultiDevicePreview
private fun NoActivePlanScreenPreview() {
    PreviewTheme { NoActivePlanScreen(onAction = {}) }
}

@Composable
@MultiDevicePreview
private fun EditBottomSheetPreview() {
    PreviewTheme { EditBottomSheetContent(onDismissRequest = {}, onAction = {}) }
}
