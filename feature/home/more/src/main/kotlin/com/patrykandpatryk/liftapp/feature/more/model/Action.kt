package com.patrykandpatryk.liftapp.feature.more.model

import com.patrykandpatryk.liftapp.feature.more.navigation.Destination

sealed class Action {
    class NavigateTo(val destination: Destination) : Action()
}
