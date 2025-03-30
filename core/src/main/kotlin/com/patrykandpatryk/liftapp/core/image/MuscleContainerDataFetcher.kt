package com.patrykandpatryk.liftapp.core.image

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MuscleContainerDataFetcher(
    private val muscleContainer: MuscleContainer,
    private val isDark: Boolean,
    private val muscleImageProvider: MuscleImageProvider,
) : DataFetcher<InputStream> {

    private var loadJob: Job? = null

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        loadJob =
            scope.launch {
                val path = muscleImageProvider.getMuscleImagePath(muscleContainer, isDark)
                runCatching { File(path).inputStream() }
                    .onSuccess { callback.onDataReady(it) }
                    .onFailure { throwable ->
                        if (throwable is Exception) {
                            callback.onLoadFailed(throwable)
                        } else {
                            throw throwable
                        }
                    }
            }
    }

    override fun cleanup() {
        scope.cancel()
        loadJob = null
    }

    override fun cancel() {
        if (loadJob == null) scope.cancel()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL
}
