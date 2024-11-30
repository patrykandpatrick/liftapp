package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

private object ScrollType {
    const val WHEEL = "Wheel"
    const val PAGER = "Pager"
}

private val Float.index: Int get() = if (this - toInt() >= 0.5f) toInt() + 1 else toInt()

private val Float.offset: Float get() = this - index

@Composable
fun ScrollSyncEffect(wheelPickerState: WheelPickerState, pagerState: PagerState) {
    LaunchedEffect(pagerState, wheelPickerState) {
        merge(
            wheelPickerState.interactionSource.interactions.map { ScrollType.WHEEL },
            pagerState.interactionSource.interactions.map { ScrollType.PAGER },
        ).flatMapLatest { scrollType ->
            when (scrollType) {
                ScrollType.WHEEL -> {
                    snapshotFlow { wheelPickerState.coercedCurrentItemWithOffset}
                        .onEach { indexWithFraction ->
                            pagerState.scrollToPage(indexWithFraction.index, indexWithFraction.offset)
                        }
                }

                ScrollType.PAGER -> {
                    snapshotFlow { pagerState.currentPage + pagerState.currentPageOffsetFraction }
                        .onEach { indexWithFraction ->
                            wheelPickerState.scrollTo(indexWithFraction.index, indexWithFraction.offset)
                        }
                }

                else -> error("Unknown scroll type: $scrollType")
            }
        }.collect()
    }
}

private val WheelPickerState.coercedCurrentItemWithOffset: Float
    get() = (currentItem + currentItemOffset).coerceIn(0f, itemCount - 1f)
