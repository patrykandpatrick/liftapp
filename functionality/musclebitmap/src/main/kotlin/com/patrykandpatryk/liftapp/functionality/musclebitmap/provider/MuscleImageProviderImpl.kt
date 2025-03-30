@file:Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")

package com.patrykandpatryk.liftapp.functionality.musclebitmap.provider

import android.graphics.Bitmap.CompressFormat.WEBP
import android.graphics.Bitmap.CompressFormat.WEBP_LOSSY
import android.os.Build
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatryk.liftapp.functionality.musclebitmap.MuscleBitmapConfig
import com.patrykandpatryk.liftapp.functionality.musclebitmap.MuscleBitmapGenerator
import com.patrykandpatryk.liftapp.functionality.musclebitmap.model.NameInfoEncoder
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

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
    private val configProvider: Provider<MuscleBitmapConfig>,
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
                Timber.d("Created targetDir")
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

            Timber.d("targetFile=${targetFile.path}")

            if (targetFile.exists().not()) {
                val bitmap =
                    withContext(defaultDispatcher) {
                        Timber.d("Generating bitmap")
                        muscleImageGeneratorImpl
                            .generateBitmap(
                                config = configProvider.get(),
                                primaryMuscles = primaryMuscles,
                                secondaryMuscles = secondaryMuscles,
                                tertiaryMuscles = tertiaryMuscles,
                            )
                            .also { Timber.d("Bitmap generated") }
                    }

                val outputStream = targetFile.outputStream()
                Timber.d("Writing bitmap to file")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bitmap.compress(WEBP_LOSSY, QUALITY, outputStream)
                } else {
                    bitmap.compress(WEBP, QUALITY, outputStream)
                }
                Timber.d("Bitmap saved")
                outputStream.close()
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
}
