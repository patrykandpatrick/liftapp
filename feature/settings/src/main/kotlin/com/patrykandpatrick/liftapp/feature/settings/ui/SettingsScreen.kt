package com.patrykandpatrick.liftapp.feature.settings.ui

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.v2.OssLicensesMenuActivity
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.stringResourceId
import com.patrykandpatrick.liftapp.core.ui.AutoBackupListItem
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.date.firstDayOfWeekOptions
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.settings.model.Action
import com.patrykandpatrick.liftapp.feature.settings.viewmodel.SettingsViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
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
    val context = LocalContext.current
    val openSourceLicensesTitle = stringResource(R.string.settings_open_source_licenses)
    val licensesColorScheme = MaterialTheme.colorScheme
    val licensesTypography = MaterialTheme.typography
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
        LazyColumn(contentPadding = paddingValues, modifier = Modifier.fillMaxHeight()) {
            item {
                EnumPreferenceListItem(
                    title = stringResource(id = R.string.settings_appearance),
                    selectedValue = allPreferences?.theme,
                    values = Theme.entries.toTypedArray(),
                    imageVector = LiftAppIcons.SunMoon,
                    getValueTitle = { stringResource(id = it.stringResourceId) },
                    onValueChange = { viewModel.onAction(Action.SetTheme(it)) },
                )
            }

            item {
                AutoBackupListItem(
                    settings = autoBackup,
                    onClick = { viewModel.onAction(Action.AutomaticBackup) },
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
                )
            }

            item { ListSectionTitle(title = stringResource(R.string.settings_about)) }

            item {
                ListItem(
                    title = openSourceLicensesTitle,
                    imageVector = LiftAppIcons.Book,
                    onClick = {
                        OssLicensesMenuActivity.setActivityTitle(openSourceLicensesTitle)
                        OssLicensesMenuActivity.setTheme(
                            licensesColorScheme,
                            licensesColorScheme,
                            licensesTypography,
                        )
                        context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                    },
                )
            }
        }
    }
}

/** The long, in-context day name, which is what `Calendar.LONG` gave the published app. */
private val DayOfWeek.displayName: String
    get() = getDisplayName(TextStyle.FULL, Locale.getDefault())
