package com.patrykandpatryk.liftapp.functionality.musclebitmap.provider

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.functionality.musclebitmap.R
import javax.inject.Inject

class ResourceBitmapProviderImpl @Inject constructor(
    private val resources: Resources,
) : ResourceBitmapProvider {

    override fun getBodyFrontBitmap(): Bitmap =
        getBitmap(R.drawable.body_front)

    override fun getBodyRearBitmap(): Bitmap =
        getBitmap(R.drawable.body_back)

    override fun getMuscleBitmap(muscle: Muscle, isFront: Boolean): Bitmap =
        getBitmap(
            drawableRes = if (isFront) {
                muscle.frontDrawableRes
            } else {
                muscle.rearDrawableRes
            },
        )

    private fun getBitmap(@DrawableRes drawableRes: Int): Bitmap =
        BitmapFactory.decodeResource(resources, drawableRes)
}

val Muscle.rearDrawableRes: Int
    get() = when (this) {
        Muscle.Adductors -> R.drawable.body_adductors_back
        Muscle.Calves -> R.drawable.body_calves
        Muscle.Forearms -> R.drawable.body_forearms_back
        Muscle.Glutes -> R.drawable.body_glutes
        Muscle.Hamstrings -> R.drawable.body_hamstrings
        Muscle.Lats -> R.drawable.body_lats
        Muscle.LowerBack -> R.drawable.body_lower_back
        Muscle.Traps -> R.drawable.body_traps
        Muscle.Shoulders -> R.drawable.body_shoulders_back
        Muscle.Triceps -> R.drawable.body_triceps
        else -> error("${this.name} is not a rear muscle.")
    }

val Muscle.frontDrawableRes: Int
    get() = when (this) {
        Muscle.Abs -> R.drawable.body_abs
        Muscle.Abductors -> R.drawable.body_abductors
        Muscle.Adductors -> R.drawable.body_adductors
        Muscle.Biceps -> R.drawable.body_bicep
        Muscle.Chest -> R.drawable.body_chest
        Muscle.Forearms -> R.drawable.body_forearms
        Muscle.Quadriceps -> R.drawable.body_quads
        Muscle.Shoulders -> R.drawable.body_shoulders
        else -> error("${this.name} is not a front muscle.")
    }
