package com.patrykandpatrick.liftapp.feature.settings.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.extension.stringResourceId
import com.patrykandpatrick.liftapp.core.ui.AutoBackupListItem
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.date.firstDayOfWeekOptions
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.settings.model.Action
import com.patrykandpatrick.liftapp.feature.settings.viewmodel.SettingsViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Book
import com.patrykandpatrick.liftapp.ui.icons.CalendarDays
import com.patrykandpatrick.liftapp.ui.icons.Clock
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Ruler
import com.patrykandpatrick.liftapp.ui.icons.Scale
import com.patrykandpatrick.liftapp.ui.icons.SunMoon
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val openSourceLicensesTitle = stringResource(R.string.settings_open_source_licenses)
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val viewModel = hiltViewModel<SettingsViewModel>()
    val allPreferences by viewModel.allPreferences.collectAsState(initial = null)
    val autoBackup by viewModel.autoBackup.collectAsStateWithLifecycle()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_settings),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { viewModel.onAction(Action.PopBackStack) },
            )
        },
    ) { paddingValues ->
        val listItemModifier = Modifier.padding(horizontal = dimens.screen.padding)
        LazyColumn(
            contentPadding =
                paddingValues.increaseBy(
                    top = dimens.screen.padding,
                    bottom = dimens.screen.padding,
                ),
            modifier = Modifier.fillMaxHeight(),
        ) {
            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.settings_appearance),
                    selectedValue = allPreferences?.theme,
                    values = Theme.entries.toTypedArray(),
                    imageVector = LiftAppIcons.SunMoon,
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = { viewModel.onAction(Action.SetTheme(it)) },
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 0, count = 2),
                )
            }

            item {
                AutoBackupListItem(
                    settings = autoBackup,
                    onClick = { viewModel.onAction(Action.AutomaticBackup) },
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 1, count = 2),
                )
            }

            item { ListSectionTitle(title = stringResource(id = R.string.units)) }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.distance),
                    selectedValue = allPreferences?.longDistanceUnit,
                    values = LongDistanceUnit.entries.toTypedArray(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = { viewModel.onAction(Action.SetDistanceUnit(it)) },
                    imageVector = LiftAppIcons.Ruler,
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 0, count = 2),
                )
            }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.mass),
                    selectedValue = allPreferences?.massUnit,
                    values = MassUnit.entries.toTypedArray(),
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = { viewModel.onAction(Action.SetMassUnit(it)) },
                    imageVector = LiftAppIcons.Scale,
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 1, count = 2),
                )
            }

            item { ListSectionTitle(title = stringResource(id = R.string.settings_time_and_date)) }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.settings_hour_format),
                    selectedValue = allPreferences?.hourFormat,
                    values = HourFormat.entries.toTypedArray(),
                    imageVector = LiftAppIcons.Clock,
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = { viewModel.onAction(Action.SetHourFormat(it)) },
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 0, count = 2),
                )
            }

            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.settings_first_day_of_week),
                    selectedValue = allPreferences?.firstDayOfWeek,
                    values = firstDayOfWeekOptions.toTypedArray(),
                    imageVector = LiftAppIcons.CalendarDays,
                    getValueTitle = { it.displayName },
                    onValueChange = { viewModel.onAction(Action.SetFirstDayOfWeek(it)) },
                    modifier = listItemModifier,
                    position = LiftAppListItemPosition(index = 1, count = 2),
                )
            }

            item { ListSectionTitle(title = stringResource(R.string.settings_about)) }

            item {
                LiftAppListItem(
                    title = openSourceLicensesTitle,
                    imageVector = LiftAppIcons.Book,
                    modifier = listItemModifier,
                    onClick = { viewModel.onAction(Action.OpenSourceLicenses) },
                )
            }
        }
    }
}

/** The long, in-context day name, which is what `Calendar.LONG` gave the published app. */
private val DayOfWeek.displayName: String
    get() = getDisplayName(TextStyle.FULL, Locale.getDefault())
