package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.StringTextFieldState
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import java.io.Serializable
import java.util.UUID

@Stable
data class ScreenState(
    val id: Long,
    val name: StringTextFieldState,
    val description: StringTextFieldState,
    val items: List<Item>,
) {
    val isEdit: Boolean
        get() = id != ID_NOT_SET

    val canSave: Boolean
        get() = items.any { it is Item.RoutineItem }

    sealed class Item : Serializable {
        abstract val id: String

        sealed class PlanElement : Item()

        /**
         * [uuid], rather than the routine's own ID, is what tells two days apart — a plan may hold
         * the same routine more than once, and the list key has to stay unique when it does.
         */
        data class RoutineItem(
            val routine: RoutineWithExercises,
            val uuid: UUID = UUID.randomUUID(),
        ) : PlanElement() {
            override val id: String = "RoutineItem:$uuid"
        }

        data class RestItem(val uuid: UUID = UUID.randomUUID()) : PlanElement() {
            override val id: String = "RestItem:$uuid"
        }

        data object PlaceholderItem : Item() {
            override val id: String = "PlaceholderItem"

            private fun readResolve(): Any = PlaceholderItem
        }
    }
}
