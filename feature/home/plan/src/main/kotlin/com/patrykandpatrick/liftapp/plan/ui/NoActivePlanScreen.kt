package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
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
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = padding.itemVertical),
        )

        ChooseExistingButton(onAction)

        CreateNewButton(onAction)
    }
}

@Composable
private fun ChooseExistingButton(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onAction(Action.ChooseExistingPlan) }, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_open),
            contentDescription = null,
            modifier = Modifier.padding(end = LocalDimens.current.padding.itemHorizontalSmall),
        )

        Text(stringResource(R.string.plan_no_active_plan_choose_existing_cta))
    }
}

@Composable
private fun CreateNewButton(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = { onAction(Action.CreateNewPlan) }, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier.padding(end = LocalDimens.current.padding.itemHorizontalSmall),
        )
        Text(stringResource(R.string.plan_no_active_plan_create_cta))
    }
}

@Composable
internal fun EditBottomSheet(onDismissRequest: () -> Unit, onAction: (Action) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val onActionWithDismiss: (Action) -> Unit = { action ->
        coroutineScope.launch {
            sheetState.hide()
            onAction(action)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        val padding = LocalDimens.current.padding
        Column(
            verticalArrangement = Arrangement.spacedBy(padding.itemVerticalSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(horizontal = padding.contentHorizontal)
                    .padding(bottom = padding.itemVerticalSmall),
        ) {
            Text(
                text = stringResource(R.string.training_plan_change_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(padding.itemVertical))
            ChooseExistingButton(onActionWithDismiss, Modifier.fillMaxWidth())
            CreateNewButton(onActionWithDismiss, Modifier.fillMaxWidth())
        }
    }
}

@Composable
@MultiDevicePreview
private fun NoActivePlanScreenPreview() {
    LiftAppTheme { Surface { NoActivePlanScreen(onAction = {}) } }
}
