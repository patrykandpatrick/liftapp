package com.patrykandpatrick.liftapp.functionality.musclebitmap

import android.content.Context
import android.content.res.Configuration
import com.patrykandpatrick.liftapp.core.R
import javax.inject.Inject

class MuscleBitmapConfigProvider @Inject constructor(private val context: Context) {

    fun get(isDark: Boolean): MuscleBitmapConfig {
        val configuration =
            Configuration(context.resources.configuration).apply {
                val nightMode =
                    if (isDark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightMode
            }
        val themedContext = context.createConfigurationContext(configuration)

        return MuscleBitmapConfig(
            borderColor = themedContext.getColor(R.color.muscle_border),
            primaryColor = themedContext.getColor(R.color.muscle_primary),
            secondaryColor = themedContext.getColor(R.color.muscle_secondary),
            tertiaryColor = themedContext.getColor(R.color.muscle_tertiary),
            bitmapMargin = themedContext.resources.getDimensionPixelSize(R.dimen.bitmap_margin),
        )
    }
}
