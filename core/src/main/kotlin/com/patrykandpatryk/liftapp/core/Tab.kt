package com.patrykandpatryk.liftapp.core

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.ui.TabItem
import javax.annotation.concurrent.Immutable

@Immutable
data class Tab(
    @StringRes val nameRes: Int,
    val content: @Composable () -> Unit,
)

val List<Tab>.tabItems: List<TabItem>
    @Composable get() = map { tab ->
        TabItem(text = stringResource(id = tab.nameRes))
    }
