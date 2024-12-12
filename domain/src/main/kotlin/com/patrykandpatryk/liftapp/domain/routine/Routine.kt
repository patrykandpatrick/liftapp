package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET

data class Routine(val name: String, val id: Long = ID_NOT_SET)
