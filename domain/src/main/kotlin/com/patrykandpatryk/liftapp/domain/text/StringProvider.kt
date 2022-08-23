package com.patrykandpatryk.liftapp.domain.text

interface StringProvider {

    val dateFormatShort: String
    val dateFormatLong: String
    val timeFormatShort24h: String
    val timeFormatShort12h: String
    val timeFormatLong24h: String
    val timeFormatLong12h: String
}
