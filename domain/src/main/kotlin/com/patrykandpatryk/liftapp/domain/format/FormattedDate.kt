package com.patrykandpatryk.liftapp.domain.format

interface FormattedDate : Comparable<FormattedDate>, java.io.Serializable {

    val dateShort: String

    val dateLong: String

    val timeShort: String

    val timeLong: String

    val year: Int

    val month: Int

    val day: Int

    val hour: Int

    val minute: Int

    val second: Int

    val millis: Long

    companion object {

        val Empty = object : FormattedDate {

            override val dateShort: String = ""
            override val dateLong: String = ""
            override val timeShort: String = ""
            override val timeLong: String = ""
            override val year: Int = 0
            override val month: Int = 0
            override val day: Int = 0
            override val hour: Int = 0
            override val minute: Int = 0
            override val second: Int = 0
            override val millis: Long = 0

            override fun compareTo(other: FormattedDate): Int = millis.compareTo(other.millis)
        }
    }
}
