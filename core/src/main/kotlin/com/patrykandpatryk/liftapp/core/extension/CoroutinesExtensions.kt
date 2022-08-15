package com.patrykandpatryk.liftapp.core.extension

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectInComposable(collector: FlowCollector<T>) {
    LaunchedEffect(key1 = collector) {
        collect(collector)
    }
}
