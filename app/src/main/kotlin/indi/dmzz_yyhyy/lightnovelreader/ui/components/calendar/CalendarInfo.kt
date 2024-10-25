package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar

import androidx.compose.runtime.Immutable
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.OutDateStyle
import java.time.DayOfWeek

@Immutable
internal data class CalendarInfo(
    val indexCount: Int,
    private val firstDayOfWeek: DayOfWeek? = null,
    private val outDateStyle: OutDateStyle? = null,
)