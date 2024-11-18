package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarMonth

class CalendarLayoutInfo(info: LazyListLayoutInfo, private val month: (Int) -> CalendarMonth) :
    LazyListLayoutInfo by info {
    val visibleMonthsInfo: List<CalendarItemInfo>
        get() = visibleItemsInfo.map {
            CalendarItemInfo(it, month(it.index))
        }
}

class CalendarItemInfo(info: LazyListItemInfo, val month: CalendarMonth) : LazyListItemInfo by info
