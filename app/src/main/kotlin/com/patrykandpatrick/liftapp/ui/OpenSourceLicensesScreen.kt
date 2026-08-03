package com.patrykandpatrick.liftapp.ui

import android.content.res.Resources
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.R
import com.patrykandpatrick.liftapp.core.R as CoreR
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Book
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun OpenSourceLicensesScreen(
    onBackClick: () -> Unit,
    onLicenseClick: (Routes.OpenSourceLicense) -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources
    val licenses by
        produceState<List<Routes.OpenSourceLicense>?>(initialValue = null, key1 = resources) {
            value = withContext(Dispatchers.IO) { resources.readOpenSourceLicenseMetadata() }
        }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text(stringResource(CoreR.string.settings_open_source_licenses)) },
                navigationIcon = { CompactTopAppBarDefaults.BackIcon(onBackClick) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        val loadedLicenses = licenses
        if (loadedLicenses == null) {
            return@LiftAppScaffold
        } else if (loadedLicenses.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(paddingValues),
            ) {
                EmptyState(
                    icon = LiftAppIcons.Book,
                    message =
                        stringResource(CoreR.string.settings_open_source_licenses_unavailable),
                    modifier = Modifier.padding(dimens.screen.padding),
                )
            }
        } else {
            LazyColumn(
                contentPadding =
                    paddingValues.increaseBy(
                        top = dimens.screen.padding,
                        bottom = dimens.screen.padding,
                    ),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = loadedLicenses,
                    key = { _, license -> "${license.offset}:${license.length}:${license.name}" },
                ) { index, license ->
                    LiftAppListItem(
                        title = { Text(license.name) },
                        position = LiftAppListItemPosition(index, loadedLicenses.size),
                        onClick = { onLicenseClick(license) },
                        modifier = Modifier.padding(horizontal = dimens.screen.padding),
                    )
                }
            }
        }
    }
}

@Composable
internal fun OpenSourceLicenseScreen(
    license: Routes.OpenSourceLicense,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources
    val unavailableText = stringResource(CoreR.string.settings_open_source_licenses_unavailable)
    val licenseText by
        produceState<String?>(initialValue = null, resources, license, unavailableText) {
            value =
                withContext(Dispatchers.IO) {
                    resources.readOpenSourceLicense(license).ifBlank { unavailableText }
                }
        }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                title = { Text(license.name) },
                navigationIcon = { CompactTopAppBarDefaults.BackIcon(onBackClick) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        val loadedLicenseText = licenseText ?: return@LiftAppScaffold
        LazyColumn(
            contentPadding =
                paddingValues.increaseBy(
                    start = dimens.screen.padding,
                    top = dimens.screen.padding,
                    end = dimens.screen.padding,
                    bottom = dimens.screen.padding,
                ),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                SelectionContainer {
                    Text(
                        text = loadedLicenseText,
                        color = colorScheme.foreground,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

private fun Resources.readOpenSourceLicenseMetadata(): List<Routes.OpenSourceLicense> =
    runCatching {
        openRawResource(R.raw.third_party_license_metadata).bufferedReader().useLines { lines ->
            lines
                .mapNotNull { line ->
                    LICENSE_METADATA_PATTERN.matchEntire(line)?.let { match ->
                        val (offset, length, name) = match.destructured
                        Routes.OpenSourceLicense(name, offset.toInt(), length.toInt())
                    }
                }
                .toList()
        }
    }
    .getOrDefault(emptyList())
    .sortedBy { it.name.lowercase(Locale.ROOT) }

private fun Resources.readOpenSourceLicense(license: Routes.OpenSourceLicense): String =
    runCatching {
        require(license.offset >= 0 && license.length >= 0)
        val bytes = ByteArray(license.length)
        openRawResource(R.raw.third_party_licenses).use { input ->
            var remainingOffset = license.offset.toLong()
            while (remainingOffset > 0) {
                val skipped = input.skip(remainingOffset)
                check(skipped > 0) { "License offset lies outside the resource." }
                remainingOffset -= skipped
            }

            var bytesRead = 0
            while (bytesRead < bytes.size) {
                val read = input.read(bytes, bytesRead, bytes.size - bytesRead)
                check(read >= 0) { "License length lies outside the resource." }
                bytesRead += read
            }
        }
        bytes.decodeToString().trim()
    }
    .getOrDefault("")

private val LICENSE_METADATA_PATTERN = Regex("""(\d+):(\d+) (.+)""")
