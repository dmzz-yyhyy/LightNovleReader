package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.data

import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarYear
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.OutDateStyle
import java.time.DayOfWeek
import java.time.Month
import java.time.Year
import java.time.temporal.ChronoUnit

fun getCalendarYearData(
    startYear: Year,
    offset: Int,
    firstDayOfWeek: DayOfWeek,
    outDateStyle: OutDateStyle,
): CalendarYear {
    val year = startYear.plusYears(offset.toLong())
    val months = List(Month.entries.size) { index ->
        getCalendarMonthData(
            startMonth = year.atMonth(Month.JANUARY),
            offset = index,
            firstDayOfWeek = firstDayOfWeek,
            outDateStyle = outDateStyle,
        ).calendarMonth
    }
    return CalendarYear(year, months)
}

fun getYearIndex(startYear: Year, targetYear: Year): Int {
    return ChronoUnit.YEARS.between(startYear, targetYear).toInt()
}

fun getYearIndicesCount(startYear: Year, endYear: Year): Int {
    // Add one to include the start year itself!
    return getYearIndex(startYear, endYear) + 1
}
