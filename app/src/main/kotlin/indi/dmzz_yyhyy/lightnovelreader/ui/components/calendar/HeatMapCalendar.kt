package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarDay
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarWeek
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.daysOfWeek
import java.time.DayOfWeek

@Composable
internal fun HeatMapCalendarImpl(
    modifier: Modifier,
    state: HeatMapCalState,
    allowScroll: Boolean,
    hideWeek: Boolean,
    contentPadding: PaddingValues,
    dayContent: @Composable ColumnScope.(day: CalendarDay, week: CalendarWeek) -> Unit,
    weekHeader: (@Composable ColumnScope.(DayOfWeek) -> Unit)? = null,
    monthHeader: (@Composable ColumnScope.(CalendarMonth) -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!hideWeek) {
            weekHeader?.let {
                WeekHeaderColumn(
                    horizontalAlignment = Alignment.End,
                    firstDayOfWeek = state.firstDayOfWeek,
                    weekHeader = it,
                )
            }
        }
        LazyRow(
            modifier = Modifier.weight(1f),
            state = state.listState,
            userScrollEnabled = allowScroll,
            contentPadding = contentPadding,
        ) {
            items(
                count = state.calendarInfo.indexCount,
                key = { offset -> state.store[offset].yearMonth },
            ) { offset ->
                val calendarMonth = state.store[offset]
                Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                    monthHeader?.invoke(this, calendarMonth)
                    Row {
                        for (week in calendarMonth.weekDays) {
                            Column {
                                for (day in week) {
                                    dayContent(day, CalendarWeek(week))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekHeaderColumn(
    horizontalAlignment: Alignment.Horizontal,
    firstDayOfWeek: DayOfWeek,
    weekHeader: @Composable (ColumnScope.(DayOfWeek) -> Unit),
) {
    Column(
        modifier = Modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = horizontalAlignment,
    ) {
        for (dayOfWeek in daysOfWeek(firstDayOfWeek)) {
            weekHeader(dayOfWeek)
        }
    }
}
