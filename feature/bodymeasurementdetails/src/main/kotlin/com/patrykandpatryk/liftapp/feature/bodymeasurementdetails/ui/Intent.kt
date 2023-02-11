package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

sealed class Intent {

    class ExpandItem(val id: Long) : Intent()
}
