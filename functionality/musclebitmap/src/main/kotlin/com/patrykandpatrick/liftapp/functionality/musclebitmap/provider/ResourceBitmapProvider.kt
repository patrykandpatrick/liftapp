package com.patrykandpatrick.liftapp.functionality.musclebitmap.provider

import android.graphics.Bitmap
import com.patrykandpatrick.liftapp.domain.muscle.Muscle

interface ResourceBitmapProvider {

    fun getBodyFrontBitmap(): Bitmap

    fun getBodyRearBitmap(): Bitmap

    fun getMuscleBitmap(muscle: Muscle, isFront: Boolean): Bitmap
}
