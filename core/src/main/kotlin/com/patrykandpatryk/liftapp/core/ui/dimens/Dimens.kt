package com.patrykandpatryk.liftapp.core.ui.dimens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val padding: Padding = Padding(),
    val height: Height = Height(),
    val strokeWidth: Dp = 1.dp,
    val verticalItemSpacing: Dp = 16.dp,
    val iconButton: IconButton = IconButton(),
) {

    @Immutable
    data class Padding(
        val contentHorizontal: Dp = 16.dp,
        val contentHorizontalSmall: Dp = 8.dp,
        val contentVertical: Dp = 20.dp,
        val contentVerticalSmall: Dp = 12.dp,
        val itemHorizontal: Dp = 16.dp,
        val itemVertical: Dp = 16.dp,
        val segmentedButtonHorizontal: Dp = 12.dp,
        val segmentedButtonVertical: Dp = 12.dp,
        val segmentedButtonElement: Dp = 8.dp,
        val supportingTextHorizontal: Dp = 16.dp,
        val supportingTextVertical: Dp = 4.dp,
        val exercisesControlsVertical: Dp = 8.dp,
    )

    @Immutable
    data class IconButton(
        val minTouchTarget: Dp = 48.dp,
        val rippleRadius: Dp = 20.dp,
    )

    @Immutable
    data class Height(
        val searchBar: Dp = 48.dp,
    )
}

val PortraitDimens = Dimens()

val LandscapeDimens = Dimens(
    padding = Dimens.Padding(
        contentHorizontal = 56.dp,
    ),
)

val DialogDimens: Dimens
    @Composable get() =
        LocalDimens.current.run {
            copy(
                padding = padding.copy(
                    contentHorizontal = 24.dp,
                    contentVertical = 12.dp,
                ),
            )
        }

val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current
