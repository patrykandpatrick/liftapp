package com.patrykandpatrick.liftapp.feature.journal.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutPageContract
import com.patrykandpatrick.liftapp.domain.workout.Workout

/**
 * Pages through finished workouts, newest first.
 *
 * Room's own paging is not used here: it assumes one row per item, whereas the query behind
 * [GetPastWorkoutPageContract] joins a workout to every set it recorded and groups the rows back
 * afterwards. The window is chosen among workouts instead, which is what the contract takes.
 */
class PastWorkoutPagingSource(
    private val getPastWorkoutPage: GetPastWorkoutPageContract,
    private val pageSize: Int,
) : PagingSource<Int, Workout>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Workout> =
        try {
            val offset = params.key ?: 0
            val workouts = getPastWorkoutPage.getPastWorkoutPage(params.loadSize, offset)
            LoadResult.Page(
                data = workouts,
                // Refreshes use the larger initial load size. Stepping back by that size would
                // leave a gap between the refreshed window and the next prepended page, so keys
                // always advance in ordinary page-sized increments.
                prevKey = if (offset == 0) null else (offset - pageSize).coerceAtLeast(0),
                nextKey = if (workouts.size < params.loadSize) null else offset + workouts.size,
            )
        } catch (throwable: Throwable) {
            LoadResult.Error(throwable)
        }

    override fun getRefreshKey(state: PagingState<Int, Workout>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor) ?: return@let null
            page.prevKey?.plus(state.config.pageSize)
                ?: page.nextKey?.minus(page.data.size)?.coerceAtLeast(0)
        }
}
