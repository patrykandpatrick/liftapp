package com.patrykandpatryk.liftapp.core.ui.image

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumptech.glide.request.RequestOptions
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun MuscleImage(model: MuscleContainer, modifier: Modifier = Modifier) {
    GlideImage(
        imageModel = { model },
        requestOptions = { RequestOptions().centerInside() },
        modifier = modifier.fillMaxWidth(),
    )
}
