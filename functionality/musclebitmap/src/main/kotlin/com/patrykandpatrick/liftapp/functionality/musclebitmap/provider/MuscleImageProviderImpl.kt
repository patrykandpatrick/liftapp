@file:Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")

package com.patrykandpatrick.liftapp.functionality.musclebitmap.provider

import android.graphics.Bitmap.CompressFormat.WEBP
import android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
import android.os.Build
import com.patrykandpatrick.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatrick.liftapp.functionality.musclebitmap.MuscleBitmapConfigProvider
import com.patrykandpatrick.liftapp.functionality.musclebitmap.MuscleBitmapGenerator
import com.patrykandpatrick.liftapp.functionality.musclebitmap.model.NameInfoEncoder
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val TARGET_SUBDIRECTORY = "muscle_images"

private const val QUALITY = 100

class MuscleImageProviderImpl
@Inject
constructor(
    private val muscleImageGeneratorImpl: MuscleBitmapGenerator,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val nameInfoEncoder: NameInfoEncoder,
    private val filesDir: File,
    private val configProvider: MuscleBitmapConfigProvider,
) : MuscleImageProvider {

    override suspend fun getMuscleImagePath(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String =
        withContext(ioDispatcher) {
            val targetDir = File(filesDir, TARGET_SUBDIRECTORY)

            if (targetDir.exists().not()) {
                targetDir.mkdirs()
            }

            val imageName =
                getMuscleImageName(
                    primaryMuscles = primaryMuscles,
                    secondaryMuscles = secondaryMuscles,
                    tertiaryMuscles = tertiaryMuscles,
                    isDark = isDark,
                )

            val targetFile = File(targetDir, imageName)

            imageGenerationMutex.withLock {
                if (!targetFile.isFile || targetFile.length() == 0L) {
                    val bitmap =
                        withContext(defaultDispatcher) {
                            muscleImageGeneratorImpl.generateBitmap(
                                config = configProvider.get(isDark),
                                primaryMuscles = primaryMuscles,
                                secondaryMuscles = secondaryMuscles,
                                tertiaryMuscles = tertiaryMuscles,
                            )
                        }

                    val temporaryFile = File.createTempFile("$imageName.", ".tmp", targetDir)
                    try {
                        val compressed =
                            temporaryFile.outputStream().use { outputStream ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    bitmap.compress(WEBP_LOSSY, QUALITY, outputStream)
                                } else {
                                    bitmap.compress(WEBP, QUALITY, outputStream)
                                }
                            }
                        check(compressed) { "Failed to compress muscle bitmap." }

                        if (targetFile.exists()) check(targetFile.delete())
                        check(temporaryFile.renameTo(targetFile)) {
                            "Failed to move muscle bitmap into the image cache."
                        }
                    } finally {
                        temporaryFile.delete()
                    }
                }
            }

            targetFile.path
        }

    override fun getMuscleImageName(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String =
        nameInfoEncoder.encodeToName(
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
            isDark = isDark,
        )

    override suspend fun invalidateMuscleImage(path: String) {
        withContext(ioDispatcher) {
            imageGenerationMutex.withLock {
                val file = File(path)
                check(file.delete() || !file.exists()) { "Failed to delete invalid muscle bitmap." }
            }
        }
    }

    private companion object {
        val imageGenerationMutex = Mutex()
    }
}
