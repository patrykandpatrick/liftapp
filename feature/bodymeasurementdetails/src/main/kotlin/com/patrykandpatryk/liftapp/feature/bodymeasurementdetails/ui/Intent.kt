package com.patrykandpatryk.liftapp.feature.bodydetails.ui

sealed class Intent {

    class ExpandItem(val id: Long) : Intent()
}
