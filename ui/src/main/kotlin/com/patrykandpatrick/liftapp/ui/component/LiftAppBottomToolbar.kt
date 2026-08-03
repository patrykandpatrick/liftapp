package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppBottomToolbar(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Column(modifier = modifier.fillMaxWidth().background(colorScheme.background)) {
        LiftAppHorizontalDivider()
        Box(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding(),
            content = content,
        )
    }
}
