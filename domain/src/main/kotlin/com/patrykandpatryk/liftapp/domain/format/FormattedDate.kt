package com.patrykandpatryk.liftapp.domain.format

interface FormattedDate : Comparable<FormattedDate>, java.io.Serializable {

    val dateShort: String

    val dateLong: String

    val timeShort: String

    val timeLong: String

    val millis: Long

    val hours: Int

    val minutes: Int

    val seconds: Int

    companion object {

        val Empty = object : FormattedDate {

            override val dateShort: String = ""
            override val dateLong: String = ""
            override val timeShort: String = ""
            override val timeLong: String = ""
            override val millis: Long = 0
            override val hours: Int = 0
            override val minutes: Int = 0
            override val seconds: Int = 0

            override fun compareTo(other: FormattedDate): Int = millis.compareTo(other.millis)
        }
    }
}
