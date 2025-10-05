package com.patrykandpatrick.liftapp.ui.component

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowInsetsControllerCompat

val windowInsetsControllerCompat: WindowInsetsControllerCompat?
    @Composable
    get() {
        val view = LocalView.current
        val window = (view.parent as? DialogWindowProvider)?.window ?: LocalActivity.current?.window
        return remember(window) { window?.let { WindowInsetsControllerCompat(it, view) } }
    }
