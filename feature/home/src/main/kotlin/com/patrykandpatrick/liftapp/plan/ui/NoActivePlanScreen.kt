package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Open
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Plus

@Composable
internal fun NoActivePlanScreen(hasPlans: Boolean, onAction: (Action) -> Unit) {
    EmptyState(
        icon = LiftAppIcons.Plan,
        message = stringResource(R.string.plan_no_active_plan),
        modifier =
            Modifier.fillMaxSize()
                .padding(
                    start = dimens.screen.padding,
                    top = dimens.screen.padding,
                    end = dimens.screen.padding,
                    bottom = dimens.screen.padding,
                ),
    ) {
        if (hasPlans) ChooseExistingButton(onAction)
        CreateNewButton(onAction, primary = !hasPlans)
    }
}

@Composable
private fun ChooseExistingButton(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    LiftAppButton(onClick = { onAction(Action.ChooseExistingPlan) }, modifier = modifier) {
        Icon(
            imageVector = LiftAppIcons.Open,
            contentDescription = null,
        )

        Text(stringResource(R.string.plan_no_active_plan_choose_existing_cta))
    }
}

@Composable
private fun CreateNewButton(
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    LiftAppButton(
        onClick = { onAction(Action.CreateNewPlan) },
        modifier = modifier,
        colors =
            if (primary) {
                LiftAppButtonDefaults.primaryButtonColors
            } else {
                LiftAppButtonDefaults.outlinedButtonColors
            },
    ) {
        Icon(
            imageVector = LiftAppIcons.Plus,
            contentDescription = null,
        )
        Text(stringResource(R.string.plan_no_active_plan_create_cta))
    }
}

@Composable
internal fun EditBottomSheet(onDismissRequest: () -> Unit, onAction: (Action) -> Unit) {
    LiftAppModalBottomSheetWithTopAppBar(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.training_plan_change_title)) },
    ) { dismiss ->
        EditBottomSheetContent(
            onAction = { action ->
                onAction(action)
                dismiss()
            }
        )
    }
}

@Composable
private fun EditBottomSheetContent(
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = dimens.screen.padding,
                    top = 8.dp,
                    end = dimens.screen.padding,
                    bottom = 16.dp,
                ),
    ) {
        ChooseExistingButton(onAction, Modifier.fillMaxWidth())
        CreateNewButton(onAction, Modifier.fillMaxWidth())
    }
}

@Composable
@MultiDevicePreview
private fun NoActivePlanScreenPreview() {
    PreviewTheme { NoActivePlanScreen(hasPlans = true, onAction = {}) }
}

@Composable
@MultiDevicePreview
private fun EditBottomSheetPreview() {
    PreviewTheme { EditBottomSheetContent(onAction = {}) }
}
