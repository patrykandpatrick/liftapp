package com.patrykandpatryk.liftapp.feature.onerepmax.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.formatValue
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.feature.onerepmax.viewmodel.OneRepMaxViewModel

fun NavGraphBuilder.addOneRepMax() {

    composable(route = Routes.OneRepMax) {
        OneRepMax()
    }
}
@Composable
fun OneRepMax(
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel = hiltViewModel<OneRepMaxViewModel>()
    val uiState by viewModel.oneRepMaxUiStateStateFlow.collectAsState()
    val context = LocalContext.current
    val navigator = navigator

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_one_rep_max),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = navigator::popBackStack,
            )
        },
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues = paddingValues),
        ) {

            Text(
                text = uiState.massUnit?.formatValue(
                    context = context,
                    value = uiState.oneRepMax,
                    decimalPlaces = ONE_REP_MAX_DECIMAL_PLACES,
                ).orEmpty(),
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
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 32.dp,
                    end = 16.dp,
                ),
            ) {

                TextField(
                    value = uiState.massInput,
                    onValueChange = viewModel::updateMassInput,
                    isError = uiState.massInputValid.not(),
                    keyboardType = KeyboardType.Decimal,
                    label = uiState.massUnit?.let {
                        stringResource(
                            id = R.string.mass_with_unit,
                            stringResource(id = it.stringResourceId),
                        )
                    } ?: stringResource(id = R.string.mass),
                )

                TextField(
                    value = uiState.repsInput,
                    keyboardType = KeyboardType.Number,
                    onValueChange = viewModel::updateRepsInput,
                    isError = uiState.repsInputValid.not(),
                    label = stringResource(id = R.string.reps),
                )
            }

            Info(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(top = 32.dp),
            )
        }
    }
}

@Composable
private fun RowScope.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    label: String,
    keyboardType: KeyboardType,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = { Text(text = label) },
        modifier = Modifier.weight(weight = 1f),
    )
}

private const val ONE_REP_MAX_DECIMAL_PLACES = 1
