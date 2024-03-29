package com.patrykandpatryk.liftapp.functionality.musclebitmap

import android.graphics.Bitmap
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

interface MuscleBitmapGenerator {

    fun generateBitmap(
        config: MuscleBitmapConfig,
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
    ): Bitmap
}
