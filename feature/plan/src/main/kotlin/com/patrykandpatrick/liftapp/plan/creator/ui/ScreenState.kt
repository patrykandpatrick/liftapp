package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.text.StringTextFieldState
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import java.io.Serializable
import java.util.UUID

@Stable
data class ScreenState(
    val id: Long,
    val name: StringTextFieldState,
    val description: StringTextFieldState,
    val items: List<Item>,
    val error: Error?,
) {
    val isEdit: Boolean
        get() = id != ID_NOT_SET

    sealed class Item : Serializable {
        abstract val id: String

        sealed class PlanElement : Item()

        data class RoutineItem(val routine: RoutineWithExercises) : PlanElement() {
            override val id: String = "RoutineItem:${routine.id}"
        }

        data class RestItem(val uuid: UUID = UUID.randomUUID()) : PlanElement() {
            override val id: String = "RestItem:$uuid"
        }

        data object PlaceholderItem : Item() {
            override val id: String = "PlaceholderItem"

            private fun readResolve(): Any = PlaceholderItem
        }
    }

    sealed class Error {
        data object NoRoutines : Error()
    }
}

@Composable
internal fun ScreenState.Error.getText(): String =
    when (this) {
        ScreenState.Error.NoRoutines -> stringResource(R.string.training_plan_error_no_routines)
    }
