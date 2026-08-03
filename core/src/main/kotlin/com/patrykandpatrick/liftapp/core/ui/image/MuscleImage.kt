package com.patrykandpatrick.liftapp.core.ui.image

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.patrykandpatrick.liftapp.core.image.MuscleImageEntryPoint
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatrick.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun MuscleImage(model: MuscleContainer, modifier: Modifier = Modifier) {
    val isDark = colorScheme.isDarkColorScheme
    val applicationContext = LocalContext.current.applicationContext
    val imageProvider =
        remember(applicationContext) {
            EntryPoints.get(applicationContext, MuscleImageEntryPoint::class.java)
                .muscleImageProvider
        }
    val primaryMuscles = model.primaryMuscles
    val secondaryMuscles = model.secondaryMuscles
    val tertiaryMuscles = model.tertiaryMuscles
    val imageName =
        remember(imageProvider, primaryMuscles, secondaryMuscles, tertiaryMuscles, isDark) {
            imageProvider.getMuscleImageName(
                primaryMuscles = primaryMuscles,
                secondaryMuscles = secondaryMuscles,
                tertiaryMuscles = tertiaryMuscles,
                isDark = isDark,
            )
        }
    val imageBitmap by
        produceState<ImageBitmap?>(
            initialValue = MuscleImageMemoryCache[imageName],
            key1 = imageName,
        ) {
            value =
                MuscleImageMemoryCache[imageName]
                    ?: try {
                        loadMuscleImage(
                            imageName = imageName,
                            imageProvider = imageProvider,
                            primaryMuscles = primaryMuscles,
                            secondaryMuscles = secondaryMuscles,
                            tertiaryMuscles = tertiaryMuscles,
                            isDark = isDark,
                        )
                    } catch (cancellationException: CancellationException) {
                        throw cancellationException
                    } catch (throwable: Throwable) {
                        Timber.e(throwable, "Failed to load muscle bitmap")
                        null
                    }
        }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth().aspectRatio(MUSCLE_IMAGE_ASPECT_RATIO),
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private suspend fun loadMuscleImage(
    imageName: String,
    imageProvider: MuscleImageProvider,
    primaryMuscles: List<Muscle>,
    secondaryMuscles: List<Muscle>,
    tertiaryMuscles: List<Muscle>,
    isDark: Boolean,
): ImageBitmap =
    withContext(Dispatchers.IO) {
        repeat(2) {
            val path =
                imageProvider.getMuscleImagePath(
                    primaryMuscles = primaryMuscles,
                    secondaryMuscles = secondaryMuscles,
                    tertiaryMuscles = tertiaryMuscles,
                    isDark = isDark,
                )
            BitmapFactory.decodeFile(path)?.let { bitmap ->
                return@withContext bitmap.asImageBitmap().also {
                    MuscleImageMemoryCache[imageName] = it
                }
            }

            Timber.w("Muscle bitmap could not be decoded; deleting it before retry")
            imageProvider.invalidateMuscleImage(path)
        }
        error("Muscle bitmap could not be decoded after regeneration.")
    }

private object MuscleImageMemoryCache {
    private const val MAX_ENTRIES = 8
    private val entries =
        object : LinkedHashMap<String, ImageBitmap>(MAX_ENTRIES, 0.75f, true) {
            override fun removeEldestEntry(
                eldest: MutableMap.MutableEntry<String, ImageBitmap>?
            ): Boolean = size > MAX_ENTRIES
        }

    @Synchronized operator fun get(key: String): ImageBitmap? = entries[key]

    @Synchronized
    operator fun set(key: String, value: ImageBitmap) {
        entries[key] = value
    }
}

private const val MUSCLE_IMAGE_ASPECT_RATIO = 0.9f
