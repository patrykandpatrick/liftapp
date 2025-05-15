package com.patrykandpatryk.liftapp.core.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(backgroundColor = 0xFFFFFFFF)
@Preview(backgroundColor = 0xFF212121, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(backgroundColor = 0xFFFFFFFF, widthDp = 720, heightDp = 360)
@Preview(
    backgroundColor = 0xFF212121,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 720,
    heightDp = 360,
)
annotation class MultiDevicePreview
