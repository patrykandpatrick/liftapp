package com.patrykandpatryk.liftapp.bodyentry.ui

sealed class Intent {

    object Save : Intent()

    class SetValue(val index: Int, val value: String) : Intent()

    class IncrementValue(val index: Int, val incrementBy: Float) : Intent()

    class SetTime(val hour: Int, val minute: Int) : Intent()

    class SetDate(val year: Int, val month: Int, val day: Int) : Intent()
}
