package com.patrykandpatrick.liftapp.feature.newroutine.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.StringTextFieldState

@Stable data class NewRoutineState(val name: StringTextFieldState, val isEdit: Boolean)
