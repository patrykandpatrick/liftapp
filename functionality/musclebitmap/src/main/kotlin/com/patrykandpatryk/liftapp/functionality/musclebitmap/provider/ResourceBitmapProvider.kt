package com.patrykandpatryk.liftapp.functionality.musclebitmap.provider

import android.graphics.Bitmap
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

interface ResourceBitmapProvider {

    fun getBodyFrontBitmap(): Bitmap

    fun getBodyRearBitmap(): Bitmap

    fun getMuscleBitmap(muscle: Muscle, isFront: Boolean): Bitmap
}
