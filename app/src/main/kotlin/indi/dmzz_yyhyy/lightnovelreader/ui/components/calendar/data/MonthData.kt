package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.data

import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarDay
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.DayPosition
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.OutDateStyle
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.atStartOfMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.daysUntil
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.yearMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.previousMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.nextMonth
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.ChronoUnit

data class MonthData internal constructor(
    private val month: YearMonth,
    private val inDays: Int,
    private val outDays: Int,
) {
    private val totalDays: Int = inDays + month.lengthOfMonth() + outDays

    private val firstDay = month.atStartOfMonth().minusDays(inDays.toLong())

    private val rows: List<List<Int>> = (0 until totalDays).chunked(7)

    private val previousMonth: YearMonth = month.previousMonth
    private val nextMonth: YearMonth = month.nextMonth

    val calendarMonth: CalendarMonth =
        CalendarMonth(month, rows.map { week -> week.map { dayOffset -> getDay(dayOffset) } })

    private fun getDay(dayOffset: Int): CalendarDay {
        val date = firstDay.plusDays(dayOffset.toLong())
        val position = when (date.yearMonth) {
            month -> DayPosition.MonthDate
            previousMonth -> DayPosition.InDate
            nextMonth -> DayPosition.OutDate
            else -> throw IllegalArgumentException("Invalid date: $date in month: $month")
        }
        return CalendarDay(date, position)
    }
}

fun getCalendarMonthData(
    startMonth: YearMonth,
    offset: Int,
    firstDayOfWeek: DayOfWeek,
    outDateStyle: OutDateStyle,
): MonthData {
    val month = startMonth.plusMonths(offset.toLong())
    val firstDay = month.atStartOfMonth()

    val inDays = firstDayOfWeek.daysUntil(firstDay.dayOfWeek)

    val outDays = (inDays + month.lengthOfMonth()).let { inAndMonthDays ->
        val endOfRowDays = if (inAndMonthDays % 7 != 0) 7 - (inAndMonthDays % 7) else 0
        val endOfGridDays = if (outDateStyle == OutDateStyle.EndOfGrid) {
            0
        } else {
            val weeksInMonth = (inAndMonthDays + endOfRowDays) / 7
            (6 - weeksInMonth) * 7
        }
        endOfRowDays + endOfGridDays
    }
    return MonthData(month, inDays, outDays)
}

fun getHeatMapCalendarMonthData(
    startMonth: YearMonth,
    offset: Int,
    firstDayOfWeek: DayOfWeek,
): MonthData {
    val month = startMonth.plusMonths(offset.toLong())
    val firstDay = month.atStartOfMonth()

    val inDays = if (offset == 0) {
        firstDayOfWeek.daysUntil(firstDay.dayOfWeek)
    } else {
        -firstDay.dayOfWeek.daysUntil(firstDayOfWeek)
    }

    val outDays = (inDays + month.lengthOfMonth()).let { inAndMonthDays ->
        if (inAndMonthDays % 7 != 0) 7 - (inAndMonthDays % 7) else 0
    }
    return MonthData(month, inDays, outDays)
}

fun getMonthIndex(startMonth: YearMonth, targetMonth: YearMonth): Int {
    return ChronoUnit.MONTHS.between(startMonth, targetMonth).toInt()
}

fun getMonthIndicesCount(startMonth: YearMonth, endMonth: YearMonth): Int {
    return getMonthIndex(startMonth, endMonth) + 1
}
