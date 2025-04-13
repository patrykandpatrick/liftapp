package com.patrykandpatryk.liftapp.core.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.patrykandpatryk.liftapp.core.exception.getUIMessage
import com.patrykandpatryk.liftapp.core.ui.error.Error
import com.patrykandpatryk.liftapp.domain.model.Loadable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun <T : Any> Loadable<T>.Unfold(
    modifier: Modifier = Modifier,
    onLoading: @Composable BoxScope.() -> Unit = {},
    onError: @Composable (BoxScope.(Throwable) -> Unit)? = { Error(message = it.getUIMessage()) },
    onSuccess: @Composable BoxScope.(T) -> Unit,
) {
    Box(modifier) {
        when (this@Unfold) {
            is Loadable.Loading -> onLoading()
            is Loadable.Error -> onError?.invoke(this, error)
            is Loadable.Success -> onSuccess(data)
        }
    }
}

fun <T : Any> Loadable<T>.valueOrNull(): T? =
    when (this) {
        is Loadable.Success -> data
        else -> null
    }

fun <T : Any> Flow<T>.toLoadableStateFlow(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
): StateFlow<Loadable<T>> =
    map<T, Loadable<T>> { Loadable.Success(it) }
        .catch { emit(Loadable.Error(it)) }
        .stateIn(scope, started, Loadable.Loading)

fun <T : Any> Flow<Loadable<T>>.stateIn(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
): StateFlow<Loadable<T>> = stateIn(scope, started, Loadable.Loading)
