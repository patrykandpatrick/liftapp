package com.patrykandpatrick.liftapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.navOptions
import com.patrykandpatrick.liftapp.core.navigation.NavItemRoute
import com.patrykandpatrick.liftapp.navigation.BottomAppBarNavigator
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun BottomNavigationBar(
    navController: NavController,
    navigator: BottomAppBarNavigator,
    navItemRoutes: Collection<NavItemRoute<Any>>,
    modifier: Modifier = Modifier,
) {
    val currentBackStackEntry by navigator.currentDestination.collectAsStateWithLifecycle(null)
    val currentDestination by remember { derivedStateOf { currentBackStackEntry?.destination } }

    Column(modifier) {
        LiftAppHorizontalDivider()

        BoxWithConstraints(
            Modifier.background(colorScheme.background).navigationBarsPadding().height(64.dp)
        ) {
            val itemCount = navItemRoutes.size
            val horizontalPadding =
                if (itemCount > 1) {
                    ((MinimumIndicatorGutter + IndicatorWidth / 2 - maxWidth / (itemCount * 2)) /
                            (1f - 1f / itemCount))
                        .coerceAtLeast(0.dp)
                } else {
                    MinimumIndicatorGutter
                }

            Row(Modifier.fillMaxSize().padding(horizontal = horizontalPadding)) {
                navItemRoutes.forEach { menuRoute ->
                    val selected = menuRoute.isSelected(currentDestination)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (selected) return@NavigationBarItem
                            navController.navigate(
                                menuRoute.route,
                                navOptions {
                                    launchSingleTop = true
                                    restoreState = true
                                    // Keep the graph's start destination on the stack. Popping to
                                    // the graph itself can save Dashboard together with another
                                    // tab;
                                    // restoring Dashboard would then put that tab back on top.
                                    popUpTo<Routes.Home.Dashboard> { saveState = true }
                                },
                            )
                        },
                        icon = menuRoute.icon,
                        label = stringResource(id = menuRoute.titleRes),
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val indicatorBackground by
        animateColorAsState(
            if (selected) colorScheme.primaryDisabled else Color.Transparent,
            label = "navigation indicator background",
        )
    val indicatorBorder by
        animateColorAsState(
            when {
                selected -> colorScheme.primary
                isPressed -> colorScheme.outline
                else -> Color.Transparent
            },
            label = "navigation indicator border",
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier =
            Modifier.weight(1f)
                .fillMaxHeight()
                .semantics(mergeDescendants = true) {
                    contentDescription = label
                    this.selected = selected
                }
                .interactiveButtonEffect(
                    colors =
                        InteractiveBorderColors(
                            color = Color.Transparent,
                            pressedColor = Color.Transparent,
                            hoverForegroundColor = Color.Transparent,
                        ),
                    onClick = onClick,
                    interactionSource = interactionSource,
                    role = Role.Tab,
                ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(width = IndicatorWidth, height = 32.dp)
                    .background(indicatorBackground, PillShape)
                    .border(width = 1.dp, color = indicatorBorder, shape = PillShape),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.foreground,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colorScheme.foreground,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private fun NavItemRoute<*>.isSelected(currentDestination: NavDestination?): Boolean =
    currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true

private val IndicatorWidth = 56.dp
private val MinimumIndicatorGutter = 16.dp
