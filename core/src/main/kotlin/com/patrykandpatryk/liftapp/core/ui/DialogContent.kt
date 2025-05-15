package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens

@Composable
fun DialogContent(title: String, actions: @Composable () -> Unit, content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = LocalDimens.current.dialog.tonalElevation,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp),
            )

            content()

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 24.dp, bottom = 24.dp),
            ) {
                actions()
            }
        }
    }
}
