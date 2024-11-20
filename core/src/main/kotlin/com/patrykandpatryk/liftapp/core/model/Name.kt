package com.patrykandpatryk.liftapp.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.domain.model.Name

@Composable
fun Name.getDisplayName(): String =
    when (this) {
        is Name.Raw -> value
        is Name.Resource -> stringResource(resource.resourceId)
    }
