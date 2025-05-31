package com.patrykandpatrick.liftapp.ui.state

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext

abstract class AnimatedState<T, V : AnimationVector>
protected constructor(
    initialValue: T,
    private val typeColorState: TwoWayConverter<T, V>,
    private val animationSpec: AnimationSpec<T> = spring(),
) : MutableState<T> by mutableStateOf(initialValue) {

    private var lastVelocity: T = initialValue

    private var targetValue: T = initialValue

    private var lastJob: Job? = null

    suspend fun animate(value: T, delay: Long = 0L) {
        targetValue = value
        lastJob?.cancelAndJoin()
        val job = Job(currentCoroutineContext().job)
        lastJob = job

        withContext(job) {
            delay(delay)
            animate(
                typeConverter = typeColorState,
                initialValue = this@AnimatedState.value,
                targetValue = value,
                initialVelocity = lastVelocity,
                animationSpec = animationSpec,
            ) { newValue, velocity ->
                this@AnimatedState.value = newValue
                lastVelocity = velocity
            }
        }
        job.complete()
    }
}
