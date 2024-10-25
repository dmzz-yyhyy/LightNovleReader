package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core

import androidx.compose.runtime.Immutable
import java.io.Serializable
import java.time.YearMonth

@Immutable
data class CalendarMonth(
    val yearMonth: YearMonth,
    val weekDays: List<List<CalendarDay>>,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalendarMonth

        if (yearMonth != other.yearMonth) return false
        if (weekDays.first().first() != other.weekDays.first().first()) return false
        if (weekDays.last().last() != other.weekDays.last().last()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearMonth.hashCode()
        result = 31 * result + weekDays.first().first().hashCode()
        result = 31 * result + weekDays.last().last().hashCode()
        return result
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("CalendarMonth { ")
            .append("yearMonth = $yearMonth, ")
            .append("firstDay = ${weekDays.first().first()}, ")
            .append("lastDay = ${weekDays.last().last()} ")
            .append("} ")
        return stringBuilder.toString()
    }
}
