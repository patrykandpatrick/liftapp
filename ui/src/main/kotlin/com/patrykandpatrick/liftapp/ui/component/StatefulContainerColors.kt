package com.patrykandpatrick.liftapp.ui.component

data class StatefulContainerColors(
    val colors: ContainerColors,
    val checkedColors: ContainerColors = colors,
) {
    fun getColors(checked: Boolean): ContainerColors = if (checked) checkedColors else colors
}
