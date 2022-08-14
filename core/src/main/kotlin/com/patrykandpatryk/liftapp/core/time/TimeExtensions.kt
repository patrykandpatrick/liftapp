package com.patrykandpatryk.liftapp.core.time

import java.util.Calendar

val now: Long
    get() = Calendar.getInstance().timeInMillis
