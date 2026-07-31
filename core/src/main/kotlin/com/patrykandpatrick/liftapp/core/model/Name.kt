package com.patrykandpatrick.liftapp.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.domain.model.Name

@Composable
fun Name.getDisplayName(): String =
    when (this) {
        is Name.Raw -> value
        is Name.Resource -> stringResource(resource.resourceId)
    }
