package com.patrykandpatryk.liftapp.feature.more.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class Destination(
    val getRoute: () -> Any,
    @StringRes val titleResourceId: Int,
    val imageVector: ImageVector,
)
