package com.patrykandpatryk.liftapp.core.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@SuppressLint("ComposableNaming")
@Composable
@NonRestartableComposable
fun <T> Flow<T>.collectInComposable(collector: FlowCollector<T>) {
    LaunchedEffect(key1 = collector) {
        collect(collector)
    }
}
