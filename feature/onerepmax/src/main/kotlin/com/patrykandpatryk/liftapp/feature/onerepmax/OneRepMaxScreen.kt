package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.coroutines.flow.flowOf

@Composable
fun OneRepMaxScreen(
    navigator: OneRepMaxNavigator,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<OneRepMaxViewModel>()
    OneRepMaxScreen(state = viewModel.state, navigator = navigator, modifier = modifier)
}

@Composable
fun OneRepMaxScreen(
    state: OneRepMaxState,
    navigator: OneRepMaxNavigator,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val massUnit = state.massUnit.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.route_one_rep_max)) },
                navigationIcon = {
                    IconButton(onClick = navigator::back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = LocalDimens.current.padding.contentHorizontal)
                .padding(paddingValues = paddingValues),
        ) {
            Text(
                text = state.oneRepMax.collectAsStateWithLifecycle().value,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = stringResource(id = R.string.one_rep_max),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
                modifier = Modifier.padding(top = 32.dp),
            ) {
                TextField(
                    value = state.mass.value,
                    onValueChange = state::updateMass,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Decimal,
                    ),
                    label = stringResource(id = R.string.mass),
                    trailingIcon = {
                        Text(text = stringResource(id = massUnit.stringResourceId))
                    },
                    modifier = Modifier,
                )

                TextField(
                    value = state.reps.value,
                    onValueChange = state::updateReps,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                        },
                    ),
                    label = stringResource(id = R.string.reps),
                    modifier = Modifier,
                )
            }

            Info(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier
                    .padding(top = 32.dp),
            )
        }
    }
}

@Composable
private fun RowScope.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        label = { Text(text = label) },
        trailingIcon = trailingIcon,
        modifier = modifier.weight(weight = 1f),
    )
}

@MultiDevicePreview
@Composable
fun OneRepMaxPreview() {
    LiftAppTheme {
        val formatter = PreviewResource.formatter()
        OneRepMaxScreen(
            navigator = interfaceStub(),
            state = OneRepMaxState(
                getMassUnit = { flowOf(MassUnit.Kilograms) },
                formatWeight = formatter::formatWeight,
                coroutineScope = rememberCoroutineScope(),
            ),
        )
    }
}
