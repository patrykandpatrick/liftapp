package com.patrykandpatrick.liftapp.core.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.StringProviderImpl
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object PreviewResource {
    val stringProvider: StringProvider
        @Composable
        get() {
            val context = LocalContext.current
            return remember { StringProviderImpl(context) }
        }

    @Composable
    fun formatter(is24H: Boolean = true): Formatter {
        val stringProvider = stringProvider
        return remember(is24H) { Formatter(stringProvider, MutableStateFlow(is24H)) }
    }

    @Composable
    fun textFieldStateManager(
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ): TextFieldStateManager {
        val stringProvider = stringProvider
        val formatter = formatter()
        return remember { TextFieldStateManager(stringProvider, formatter, savedStateHandle) }
    }

    @Composable
    fun <T> preference(value: T): Preference<T> = remember {
        object : Preference<T> {
            val flow = MutableStateFlow(value)

            override fun get(): Flow<T> = flow

            override suspend fun set(value: T) {
                flow.value = value
            }

            override suspend fun update(function: (T) -> T) {
                flow.update(function)
            }
        }
    }
}

@Composable
fun PreviewTheme(content: @Composable () -> Unit) {
    LiftAppTheme {
        LiftAppBackground {
            CompositionLocalProvider(
                LocalMarkupProcessor provides rememberDefaultMarkupProcessor(),
                LocalFormatter provides PreviewResource.formatter(),
                content = content,
            )
        }
    }
}
