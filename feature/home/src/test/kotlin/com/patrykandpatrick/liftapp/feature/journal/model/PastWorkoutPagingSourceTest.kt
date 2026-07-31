package com.patrykandpatrick.liftapp.feature.journal.model

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutPageContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PastWorkoutPagingSourceTest {

    @Test
    fun `refresh key keeps an anchor in the initial window at offset zero`() {
        val source =
            PastWorkoutPagingSource(
                getPastWorkoutPage = GetPastWorkoutPageContract { _, _ -> emptyList() },
                pageSize = PAGE_SIZE,
            )
        val page =
            PagingSource.LoadResult.Page(
                data = List(24) { workout(it.toLong()) },
                prevKey = null,
                nextKey = 24,
            )
        val state =
            PagingState(
                pages = listOf(page),
                anchorPosition = 5,
                config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = 24),
                leadingPlaceholderCount = 0,
            )

        assertEquals(0, source.getRefreshKey(state))
    }

    @Test
    fun `refresh and prepend load contiguous windows`() = runTest {
        val source =
            PastWorkoutPagingSource(
                getPastWorkoutPage =
                    GetPastWorkoutPageContract { limit, offset ->
                        List(limit) { index -> workout((offset + index).toLong()) }
                    },
                pageSize = PAGE_SIZE,
            )

        val refresh =
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = 16,
                    loadSize = 24,
                    placeholdersEnabled = false,
                )
            ) as PagingSource.LoadResult.Page

        assertEquals(8, refresh.prevKey)
        assertEquals(40, refresh.nextKey)
        assertEquals((16L..39L).toList(), refresh.data.map(Workout::id))

        val prepend =
            source.load(
                PagingSource.LoadParams.Prepend(
                    key = checkNotNull(refresh.prevKey),
                    loadSize = PAGE_SIZE,
                    placeholdersEnabled = false,
                )
            ) as PagingSource.LoadResult.Page

        assertEquals(0, prepend.prevKey)
        assertEquals(16, prepend.nextKey)
        assertEquals((8L..15L).toList(), prepend.data.map(Workout::id))
    }

    private companion object {
        const val PAGE_SIZE = 8

        fun workout(id: Long) =
            Workout(
                id = id,
                routineID = 0,
                name = "Workout $id",
                startDate = LocalDateTime.MIN,
                endDate = LocalDateTime.MIN,
                notes = "",
                exercises = emptyList(),
            )
    }
}
