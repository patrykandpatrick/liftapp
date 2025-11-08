package com.patrykandpatryk.liftapp.core.time

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import kotlin.time.Duration

@SuppressLint("SimpleDateFormat") private val formatter = SimpleDateFormat("mm:ss")

val Duration.formattedRemainingTime: String
    get() = formatter.format(inWholeMilliseconds)
