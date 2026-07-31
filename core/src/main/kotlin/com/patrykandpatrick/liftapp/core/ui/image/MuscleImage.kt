package com.patrykandpatrick.liftapp.core.ui.image

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumptech.glide.request.RequestOptions
import com.patrykandpatrick.liftapp.core.image.MuscleImageModel
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MuscleImage(model: MuscleContainer, modifier: Modifier = Modifier) {
    val imageModel = MuscleImageModel(model, colorScheme.isDarkColorScheme)
    GlideImage(
        imageModel = { imageModel },
        requestOptions = { RequestOptions().centerInside() },
        modifier = modifier.fillMaxWidth().aspectRatio(MUSCLE_IMAGE_ASPECT_RATIO),
    )
}

private const val MUSCLE_IMAGE_ASPECT_RATIO = 0.9f
