package com.patrykandpatryk.liftapp.core.format

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.date.day
import com.patrykandpatryk.liftapp.domain.date.hour
import com.patrykandpatryk.liftapp.domain.date.minute
import com.patrykandpatryk.liftapp.domain.date.month
import com.patrykandpatryk.liftapp.domain.date.second
import com.patrykandpatryk.liftapp.domain.date.year
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

    override val year: Int = calendar.year

    override val month: Int = calendar.month

    override val day: Int = calendar.day

    override val hour: Int = calendar.hour

    override val minute: Int = calendar.minute

    override val second: Int = calendar.second

    override val millis: Long = calendar.timeInMillis

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

        return true
    }

    override fun hashCode(): Int {
        var result = dateShort.hashCode()
        result = 31 * result + dateLong.hashCode()
        result = 31 * result + timeShort.hashCode()
        result = 31 * result + timeLong.hashCode()
        result = 31 * result + millis.hashCode()
        return result
    }
}
