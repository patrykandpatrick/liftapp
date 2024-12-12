package com.patrykandpatryk.liftapp.feature.settings.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.feature.settings.navigator.SettingsNavigator
import com.patrykandpatryk.liftapp.feature.settings.viewmodel.SettingsViewModel

@Composable
fun Settings(navigator: SettingsNavigator, modifier: Modifier = Modifier) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel = hiltViewModel<SettingsViewModel>()
    val allPreferences by viewModel.allPreferences.collectAsState(initial = null)

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_settings),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = navigator::back,
            )
        },
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxHeight()) {
            item { ListSectionTitle(title = stringResource(id = R.string.units)) }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.distance),
                    selectedValue = allPreferences?.longDistanceUnit,
                    values = LongDistanceUnit.entries.toTypedArray(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = viewModel::setDistanceUnit,
                    iconPainter = painterResource(id = R.drawable.ic_distance),
                )
            }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.mass),
                    selectedValue = allPreferences?.massUnit,
                    values = MassUnit.entries.toTypedArray(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = viewModel::setMassUnit,
                    iconPainter = painterResource(id = R.drawable.ic_weight),
                )
            }

            item { ListSectionTitle(title = stringResource(id = R.string.settings_time_and_date)) }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.settings_hour_format),
                    selectedValue = allPreferences?.hourFormat,
                    values = HourFormat.entries.toTypedArray(),
                    iconPainter = painterResource(id = R.drawable.ic_time),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = viewModel::setHourFormat,
                )
            }
        }
    }
}
