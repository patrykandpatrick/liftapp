package com.patrykandpatrick.liftapp.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun GridPreviewSurface(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(16.dp),
    content: List<Pair<String, @Composable () -> Unit>>,
) {
    val reusableContent =
        @Composable { modifier: Modifier ->
            Surface(color = colorScheme.background, modifier = modifier) {
                FlowRow(
                    modifier = Modifier.padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    content.forEach { (title, content) ->
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                color = colorScheme.onSurface,
                            )
                            content()
                        }
                    }
                }
            }
        }
    Column(modifier = modifier.systemBarsPadding()) {
        LiftAppTheme(darkTheme = false) { reusableContent(Modifier.weight(1f).fillMaxWidth()) }
        LiftAppTheme(darkTheme = true) { reusableContent(Modifier.weight(1f).fillMaxWidth()) }
    }
}
