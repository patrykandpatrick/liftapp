package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize
import com.patrykandpatrick.liftapp.ui.modifier.topTintedEdge
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

    val baseScrimColor = colorScheme.bottomSheetScrim
    val screenSize = LocalWindowInfo.current.containerSize
    val (scrimColor, setScrimColor) = remember { mutableStateOf(baseScrimColor.copy(0f)) }
    val isBackgroundDark = scrimColor.compositeOver(colorScheme.surface).luminance() < 0.5

    ModalBottomSheet(
        onDismissRequest = onDismissRequest.value,
        sheetState = sheetState,
        shape = BottomSheetShape,
        dragHandle = null,
        containerColor = containerColor,
        modifier =
            modifier
                .drawBehind {
                    val offsetFraction =
                        (sheetState.requireOffset() / screenSize.height).coerceIn(0f, 1f)
                    val scrimColor = scrimColor.copy(1f - offsetFraction)
                    setScrimColor(scrimColor)
                    drawRect(color = scrimColor, size = screenSize.toSize())
                }
                .statusBarsPadding(),
        scrimColor = Color.Transparent,
        content = {
            val insetsController = windowInsetsControllerCompat

            Column(modifier = Modifier.topTintedEdge(BottomSheetShape)) {
                content(contentOnDismissRequest)
            }

            LaunchedEffect(isBackgroundDark) {
                insetsController?.isAppearanceLightStatusBars = !isBackgroundDark
            }
        },
    )
}
