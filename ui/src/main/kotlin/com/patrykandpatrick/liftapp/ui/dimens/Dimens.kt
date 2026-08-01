package com.patrykandpatrick.liftapp.ui.dimens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val badge: Badge = Badge(),
    val button: Button = Button(),
    val checkbox: Checkbox = Checkbox(),
    val chip: Chip = Chip(),
    val dialog: Dialog = Dialog(),
    val divider: Divider = Divider(),
    val fab: FAB = FAB(),
    val iconButton: IconButton = IconButton(),
    val muscle: Muscle = Muscle(),
    val progress: Progress = Progress(),
    val radioButton: RadioButton = RadioButton(),
    val ratioBar: RatioBar = RatioBar(),
    val routine: Routine = Routine(),
    val screen: Screen = Screen(),
    val segmentedButton: SegmentedButton = SegmentedButton(),
    val stepper: Stepper = Stepper(),
    val supportingText: SupportingText = SupportingText(),
    val tab: Tab = Tab(),
) {

    @Immutable
    data class Screen(
        val horizontalPadding: Dp = 16.dp,
        val verticalPadding: Dp = 20.dp,
    )

    @Immutable
    data class IconButton(
        val minTouchTarget: Dp = 48.dp,
        val size: Dp = 40.dp,
    )

    @Immutable
    data class Dialog(
        val minWidth: Dp = 280.dp,
        val maxWidth: Dp = 560.dp,
        val windowPadding: Dp = 24.dp,
        val contentPadding: Dp = 24.dp,
        val tonalElevation: Dp = 6.dp,
    )

    @Immutable
    data class Chip(
        val iconSize: Dp = 18.dp,
        val spacing: Dp = 4.dp,
        val horizontalPadding: Dp = 6.dp,
        val verticalPadding: Dp = 6.dp,
        val minHeight: Dp = 20.dp,
    )

    /**
     * A badge is smaller than a chip because nothing taps it: it needs to be read, not hit, so it
     * is free of the touch target a chip has to carry. The compact sizes are for a badge sharing a
     * line with text that needs the rest of the room.
     */
    @Immutable
    data class Badge(
        val iconSize: Dp = 14.dp,
        val spacing: Dp = 2.dp,
        val horizontalPadding: Dp = 8.dp,
        val verticalPadding: Dp = 3.dp,
        val borderWidth: Dp = 1.dp,
        val compactIconSize: Dp = 12.dp,
        val compactHorizontalPadding: Dp = 5.dp,
        val compactVerticalPadding: Dp = 1.dp,
    )

    @Immutable
    data class Muscle(
        val tileSize: Dp = 20.dp,
        val tileCornerSize: Dp = 6.dp,
        val gridCellMinSize: Dp = 164.dp,
        val listItemHorizontalMargin: Dp = 16.dp,
    )

    @Immutable
    data class Checkbox(val size: Dp = 20.dp, val cornerSize: Dp = 4.dp, val strokeWidth: Dp = 2.dp)

    @Immutable data class RadioButton(val size: Dp = 20.dp)

    @Immutable data class Progress(val thickness: Dp = 5.dp)

    /** Thinner than a [Progress] bar, which has one weight to carry rather than two. */
    @Immutable data class RatioBar(val thickness: Dp = 4.dp, val spacing: Dp = 3.dp)

    @Immutable data class Routine(val minCardWidth: Dp = 140.dp)

    @Immutable data class Tab(val verticalPadding: Dp = 16.dp, val iconToTextPadding: Dp = 4.dp)

    @Immutable
    data class Button(
        val iconPadding: Dp = 8.dp,
        val horizontalPadding: Dp = 16.dp,
        val verticalPadding: Dp = 10.dp,
        val minContentHeight: Dp = 24.dp,
        val underlineWidth: Dp = 1.5.dp,
    )

    @Immutable
    data class FAB(
        val iconPadding: Dp = 12.dp,
        val horizontalPadding: Dp = 20.dp,
        val verticalPadding: Dp = 14.dp,
    )

    @Immutable
    data class SegmentedButton(
        val horizontalPadding: Dp = 12.dp,
        val verticalPadding: Dp = 12.dp,
        val elementSpacing: Dp = 8.dp,
        val borderWidth: Dp = 1.dp,
    )

    @Immutable
    data class Stepper(
        val stepBorderPaddingVertical: Dp = 6.dp,
        val stepBorderPaddingHorizontal: Dp = 10.dp,
        val stepIconSize: Dp = 12.dp,
        val spacing: Dp = 4.dp,
    )

    @Immutable
    data class SupportingText(
        val horizontalPadding: Dp = 16.dp,
        val verticalPadding: Dp = 4.dp,
    )

    @Immutable
    data class Divider(
        val sinPeriodLength: Dp = 3.dp,
        val sinHeight: Dp = 6.dp,
        val thickness: Dp = 1.dp,
    )
}

val PortraitDimens = Dimens()

val LandscapeDimens = Dimens(screen = Dimens.Screen(horizontalPadding = 56.dp))

val LocalDimens = staticCompositionLocalOf { Dimens() }

val dimens: Dimens
    @Composable @ReadOnlyComposable get() = LocalDimens.current
