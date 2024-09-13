package com.patrykandpatryk.liftapp.functionality.musclebitmap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.PorterDuffColorFilter
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.functionality.musclebitmap.provider.ResourceBitmapProvider
import javax.inject.Inject

class MuscleBitmapGeneratorImpl @Inject constructor(
    private val resourceBitmapProvider: ResourceBitmapProvider,
) : MuscleBitmapGenerator {

    private val borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val primaryPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val secondaryPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val tertiaryPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun generateBitmap(
        config: MuscleBitmapConfig,
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
    ): Bitmap {
        applyConfig(config)

        val frontBitmap = resourceBitmapProvider.getBodyFrontBitmap()
        val rearBitmap = resourceBitmapProvider.getBodyRearBitmap()

        val frontBitmapWidth = frontBitmap.width * 2
        val rearBitmapStartOffset = (frontBitmapWidth + config.bitmapMargin).toFloat()

        val wholeBitmap = Bitmap.createBitmap(
            frontBitmapWidth * 2 + config.bitmapMargin,
            frontBitmap.height,
            frontBitmap.config ?: Bitmap.Config.ARGB_8888,
        )

        wholeBitmap.prepareToDraw()
        val canvas = Canvas(wholeBitmap)

        tertiaryMuscles.forEach { muscle ->
            canvas.drawBitmapOnBothSides(muscle, tertiaryPaint, rearBitmapStartOffset)
        }

        secondaryMuscles.forEach { muscle ->
            canvas.drawBitmapOnBothSides(muscle, secondaryPaint, rearBitmapStartOffset)
        }

        primaryMuscles.forEach { muscle ->
            canvas.drawBitmapOnBothSides(muscle, primaryPaint, rearBitmapStartOffset)
        }

        canvas.drawBitmapOnBothSides(
            bitmap = frontBitmap,
            paint = borderPaint,
            left = 0f,
        )

        canvas.drawBitmapOnBothSides(
            bitmap = rearBitmap,
            paint = borderPaint,
            left = rearBitmapStartOffset,
        )

        return wholeBitmap
    }

    private fun applyConfig(config: MuscleBitmapConfig) {
        borderPaint.colorFilter = PorterDuffColorFilter(config.borderColor, SRC_IN)
        primaryPaint.colorFilter = PorterDuffColorFilter(config.primaryColor, SRC_IN)
        secondaryPaint.colorFilter = PorterDuffColorFilter(config.secondaryColor, SRC_IN)
        tertiaryPaint.colorFilter = PorterDuffColorFilter(config.tertiaryColor, SRC_IN)
    }

    private fun Canvas.drawBitmapOnBothSides(
        muscle: Muscle,
        paint: Paint,
        rearBitmapStartOffset: Float,
    ) {
        if (muscle.isFrontMuscle) {
            drawBitmapOnBothSides(
                bitmap = resourceBitmapProvider.getMuscleBitmap(muscle = muscle, isFront = true),
                paint = paint,
                left = 0f,
            )
        }
        if (muscle.isRearMuscle) {
            drawBitmapOnBothSides(
                bitmap = resourceBitmapProvider.getMuscleBitmap(muscle = muscle, isFront = false),
                paint = paint,
                left = rearBitmapStartOffset,
            )
        }
    }

    private fun Canvas.drawBitmapOnBothSides(
        bitmap: Bitmap,
        paint: Paint,
        left: Float,
    ) {
        drawBitmap(bitmap, left, 0f, paint)

        val checkpoint = save()

        translate((left + bitmap.width) * 2f, 0f)
        scale(-1f, 1f)
        drawBitmap(bitmap, left, 0f, paint)

        restoreToCount(checkpoint)
    }
}

internal val Muscle.isFrontMuscle: Boolean
    get() = when (this) {
        Muscle.Abs,
        Muscle.Abductors,
        Muscle.Adductors,
        Muscle.Biceps,
        Muscle.Chest,
        Muscle.Forearms,
        Muscle.Quadriceps,
        Muscle.Shoulders,
        -> true

        else -> false
    }

internal val Muscle.isRearMuscle: Boolean
    get() = when (this) {
        Muscle.Adductors,
        Muscle.Calves,
        Muscle.Forearms,
        Muscle.Glutes,
        Muscle.Hamstrings,
        Muscle.Lats,
        Muscle.LowerBack,
        Muscle.Traps,
        Muscle.Shoulders,
        Muscle.Triceps,
        -> true

        else -> false
    }
