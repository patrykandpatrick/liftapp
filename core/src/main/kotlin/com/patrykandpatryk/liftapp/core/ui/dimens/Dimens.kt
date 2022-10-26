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
    val button: Button = Button(),
    val card: Card = Card(),
    val chip: Chip = Chip(),
    val dialog: Dialog = Dialog(),
    val height: Height = Height(),
    val iconButton: IconButton = IconButton(),
    val input: Input = Input(),
    val padding: Padding = Padding(),
    val routine: Routine = Routine(),
    val strokeWidth: Dp = 1.dp,
    val verticalItemSpacing: Dp = 16.dp,
) {

    @Immutable
    data class Padding(
        val contentHorizontal: Dp = 16.dp,
        val contentHorizontalSmall: Dp = 8.dp,
        val contentVertical: Dp = 20.dp,
        val contentVerticalSmall: Dp = 10.dp,
        val itemHorizontal: Dp = 16.dp,
        val itemHorizontalSmall: Dp = 8.dp,
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

    @Immutable
    data class Dialog(
        val minWidth: Dp = 280.dp,
        val maxWidth: Dp = 560.dp,
        val paddingMedium: Dp = 16.dp,
        val paddingLarge: Dp = 24.dp,
        val tonalElevation: Dp = 6.dp,
    )

    @Immutable
    data class Card(
        val smallElevation: Dp = 4.dp,
        val smallCornerRadius: Dp = 16.dp,
        val contentPaddingHorizontal: Dp = 16.dp,
        val contentPaddingVertical: Dp = 16.dp,
    )

    @Immutable
    data class Chip(
        val iconSize: Dp = 18.dp,
    )

    @Immutable
    data class Input(
        val nameMaxLines: Int = 3,
    )

    @Immutable
    data class Routine(
        val minCardWidth: Dp = 140.dp,
    )

    @Immutable
    data class Button(
        val iconPadding: Dp = 8.dp,
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
                    contentVertical = 24.dp,
                ),
            )
        }

val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current
