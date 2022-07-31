package com.patrykandpatryk.liftapp.bodyrecord.ui

typealias BodyActionHandler = (action: BodyAction) -> Unit

sealed class BodyAction {

    object Save : BodyAction()

    class SetValue(val index: Int, val value: String) : BodyAction()

    class IncrementValue(val index: Int, val incrementBy: Float) : BodyAction()
}
