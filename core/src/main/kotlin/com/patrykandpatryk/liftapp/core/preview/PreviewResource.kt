package com.patrykandpatryk.liftapp.core.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.core.text.StringProviderImpl
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

object PreviewResource {
    val stringProvider: StringProvider
        @Composable get() {
            val context = LocalContext.current
            return remember { StringProviderImpl(context) }
        }

    @Composable
    fun formatter(is24H: Boolean = true): Formatter {
        val stringProvider = stringProvider
        return remember(is24H) { Formatter(stringProvider, flowOf(is24H)) }
    }

    @Composable
    fun textFieldStateManager(savedStateHandle: SavedStateHandle = SavedStateHandle()): TextFieldStateManager {
        val stringProvider = stringProvider
        val formatter = formatter()
        return remember { TextFieldStateManager(stringProvider, formatter, savedStateHandle) }
    }

    @Composable
    fun <T> preference(value: T): Preference<T> =
        remember {
            object : Preference<T> {
                val flow = MutableStateFlow(value)

                override fun get(): Flow<T> = flow

                override suspend fun set(value: T) {
                    flow.value = value
                }
            }
        }
}
