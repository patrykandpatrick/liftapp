package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.component.LiftAppModalBottomSheet
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppModalBottomSheetWithTopAppBar(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor: Color = colorScheme.surface,
    title: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    LiftAppModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        containerColor = containerColor,
    ) { dismiss ->
        CompactTopAppBar(
            title = { title?.invoke() },
            navigationIcon = {
                CompactTopAppBarDefaults.IconButton(
                    imageVector = LiftAppIcons.Cross,
                    onClick = dismiss,
                )
            },
            content = null,
        )
        content(dismiss)
    }
}
