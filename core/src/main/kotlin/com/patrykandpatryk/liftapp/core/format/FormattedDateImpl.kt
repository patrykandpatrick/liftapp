package com.patrykandpatryk.liftapp.core.format

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.date.hour
import com.patrykandpatryk.liftapp.domain.date.minute
import com.patrykandpatryk.liftapp.domain.date.second
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import java.util.Calendar

@Stable
internal class FormattedDateImpl(
    override val dateShort: String,
    override val dateLong: String,
    override val timeShort: String,
    override val timeLong: String,
    calendar: Calendar,
) : FormattedDate {

    override val millis: Long = calendar.timeInMillis

    override val hours: Int = calendar.hour

    override val minutes: Int = calendar.minute

    override val seconds: Int = calendar.second

    override fun compareTo(other: FormattedDate): Int = millis.compareTo(other.millis)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FormattedDateImpl

        if (dateShort != other.dateShort) return false
        if (dateLong != other.dateLong) return false
        if (timeShort != other.timeShort) return false
        if (timeLong != other.timeLong) return false
        if (millis != other.millis) return false
        if (hours != other.hours) return false
        if (minutes != other.minutes) return false
        if (seconds != other.seconds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateShort.hashCode()
        result = 31 * result + dateLong.hashCode()
        result = 31 * result + timeShort.hashCode()
        result = 31 * result + timeLong.hashCode()
        result = 31 * result + millis.hashCode()
        result = 31 * result + hours
        result = 31 * result + minutes
        result = 31 * result + seconds
        return result
    }
}
