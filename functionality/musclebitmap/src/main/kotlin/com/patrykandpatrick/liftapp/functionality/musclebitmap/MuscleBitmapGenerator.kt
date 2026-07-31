package com.patrykandpatrick.liftapp.functionality.musclebitmap

import android.graphics.Bitmap
import com.patrykandpatrick.liftapp.domain.muscle.Muscle

interface MuscleBitmapGenerator {

    fun generateBitmap(
        config: MuscleBitmapConfig,
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
    ): Bitmap
}
