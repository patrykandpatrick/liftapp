package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.calculateEndPadding
import com.patrykandpatryk.liftapp.core.extension.calculateStartPadding
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

@Suppress("UnusedPrivateMember")
@Composable
fun Routines(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navigate: (String) -> Unit,
) {

    val dimensPadding = LocalDimens.current.padding
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxHeight()
            .padding(bottom = padding.calculateBottomPadding()),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_routine),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { },
            )
        },
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_routines),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },

    ) { internalPadding ->

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(
                    top = internalPadding.calculateTopPadding(),
                    start = internalPadding.calculateStartPadding(),
                    end = internalPadding.calculateEndPadding(),
                ),
            columns = StaggeredGridCells.Adaptive(minSize = LocalDimens.current.routine.minCardWidth),
            contentPadding = PaddingValues(
                horizontal = dimensPadding.contentHorizontalSmall,
                vertical = dimensPadding.contentVertical,
            ),
            verticalArrangement = Arrangement.spacedBy(dimensPadding.contentVerticalSmall),
            horizontalArrangement = Arrangement.spacedBy(dimensPadding.contentHorizontalSmall),
        ) {}
    }
}
