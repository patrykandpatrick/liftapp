package com.patrykandpatryk.liftapp.feature.settings.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.feature.settings.viewmodel.SettingsViewModel

@Composable
fun Settings(
    parentNavController: NavController,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = topAppBarScrollBehavior()
    val viewModel = hiltViewModel<SettingsViewModel>()
    val allPreferences by viewModel.allPreferences.collectAsState(initial = null)

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_settings),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = parentNavController::popBackStack,
            )
        },
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxHeight(),
        ) {
            item {
                ListSectionTitle(title = stringResource(id = R.string.units))
            }
            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.distance),
                    selectedValue = allPreferences?.distanceUnit,
                    values = DistanceUnit.values(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = viewModel::setDistanceUnit,
                    iconPainter = painterResource(id = R.drawable.ic_distance),
                )
            }
            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.mass),
                    selectedValue = allPreferences?.massUnit,
                    values = MassUnit.values(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = viewModel::setMassUnit,
                    iconPainter = painterResource(id = R.drawable.ic_weight),
                )
            }
        }
    }
}
