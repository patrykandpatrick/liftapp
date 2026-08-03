package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TriangleAlert
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The app's snackbar: inset from the screen edges, with the message optionally led by an icon.
 *
 * The colors default to the inverse of the surface, which is what makes a snackbar stand off the
 * screen behind it.
 */
@Composable
fun LiftAppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    containerColor: Color = colorScheme.foreground,
    contentColor: Color = colorScheme.surface,
    icon: ImageVector? = null,
) {
    SnackbarHost(
        hostState = hostState,
        modifier =
            modifier.padding(
                horizontal = dimens.screen.padding,
                vertical = 16.dp,
            ),
    ) { snackbarData ->
        Snackbar(containerColor = containerColor, contentColor = contentColor) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }

                Text(text = snackbarData.visuals.message)
            }
        }
    }
}

/** The form a failure takes: the error color, and the icon that goes with it. */
@Composable
fun LiftAppErrorSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    LiftAppSnackbarHost(
        hostState = hostState,
        modifier = modifier,
        containerColor = colorScheme.error,
        contentColor = colorScheme.onError,
        icon = LiftAppIcons.TriangleAlert,
    )
}
