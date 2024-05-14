package com.patrykandpatryk.liftapp.core.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import kotlinx.coroutines.launch

@Composable
fun BottomSheetDestination(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = dismiss,
        modifier = modifier,
        shape = BottomSheetShape,
    ) { content(dismiss) }

    BackHandler(onBack = dismiss)
}
