package com.patrykandpatrick.liftapp.feature.more.model

import com.patrykandpatrick.liftapp.feature.more.navigation.Destination

sealed class Action {
    class NavigateTo(val destination: Destination) : Action()
}
