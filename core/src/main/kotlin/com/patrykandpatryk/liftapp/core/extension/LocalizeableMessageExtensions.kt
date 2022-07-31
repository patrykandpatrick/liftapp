package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.domain.message.LocalizableMessage
import com.patrykandpatryk.liftapp.domain.validation.Validatable

@Composable
fun LocalizableMessage.getText(): String =
    stringResource(id = messageResId, formatArgs = messageArgs)

@Composable
fun <T> Validatable<T>.getMessageTextOrNull(): String? =
    (this as? Validatable.Invalid)?.message?.getText()
