package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlinx.coroutines.launch

@Composable
fun LiftAppModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor: Color = colorScheme.surface,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val onDismissRequest = rememberUpdatedState(onDismissRequest)
    val contentOnDismissRequest: () -> Unit =
        remember(coroutineScope) {
            {
                coroutineScope.launch {
                    sheetState.hide()
                    onDismissRequest.value()
                }
            }
        }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest.value,
        sheetState = sheetState,
        shape = BottomSheetShape,
        dragHandle = null,
        containerColor = containerColor,
        modifier = modifier,
        content = { content(contentOnDismissRequest) },
    )
}
