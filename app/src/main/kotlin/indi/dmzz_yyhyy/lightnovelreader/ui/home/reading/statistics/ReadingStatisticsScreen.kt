package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.AnimatedText
import indi.dmzz_yyhyy.lightnovelreader.ui.components.HeatMapCalendar
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.CalendarLayoutInfo
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.HeatMapCalState
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarDay
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.CalendarWeek
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.displayText
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core.yearMonth
import indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.rememberHeatMapCalendarState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingStatisticsScreen(
    topBar: (@Composable () -> Unit) -> Unit,
    onClickBack: () -> Unit,
    viewModel: ReadingStatisticsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val statisticsData = uiState.statisticsData
    val selectedDate = uiState.selectedDate
    val isLoading = uiState.isLoading

    val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf<Pair<LocalDate, Level>?>(null) }

    topBar {
        TopBar(
            scrollBehavior = pinnedScrollBehavior,
            onClickBack = onClickBack,
            onClickSettings = {
                showSettingsBottomSheet = true
            }
        )
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            val state = rememberHeatMapCalendarState(
                startMonth = startDate.yearMonth,
                endMonth = endDate.yearMonth,
                firstVisibleMonth = LocalDate.now().yearMonth,
                firstDayOfWeek = DayOfWeek.MONDAY
            )

            HeatMapCalendar(
                modifier = Modifier.padding(vertical = 10.dp),
                state = state,
                contentPadding = PaddingValues(end = 6.dp),
                dayContent = { day, week ->
                    val isClicked = selectedDate == day.date
                    val level = statisticsData[day.date] ?: Level.Zero
                    Day(
                        selected = isClicked,
                        day = day,
                        startDate = startDate,
                        endDate = endDate,
                        week = week,
                        level = level,
                    ) { date ->
                        selection = Pair(date, level)
                        viewModel.selectDate(date)
                    }
                },
                weekHeader = { WeekHeader(it) },
                monthHeader = { MonthHeader(it, LocalDate.now(), state) },
            )

            CalendarHint(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), viewModel)

            Box(modifier = Modifier.weight(1f)) {
                BottomContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .align(Alignment.BottomCenter),
                    selection = selection,
                    /*scrollToPrev = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                        }
                    },
                    scrollToNext = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                        }
                    },*/
                )
            }
        }
    }
}




private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

@Composable
private fun BottomContent(
    modifier: Modifier = Modifier,
    selection: Pair<LocalDate, Level>? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (selection != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                AnimatedText(text = formatter.format(selection.first))
                LevelBox(color = selection.second.color)
            }
        }

    }
}


@Composable
private fun CalendarHint(
    modifier: Modifier = Modifier,
    viewModel: ReadingStatisticsViewModel
) {
    val threshold by viewModel.threshold.collectAsState()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = "少",
            fontSize = 12.sp
        )
        Level.entries.forEach { level ->
            LevelBox(level.color)
        }
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = "多 ($threshold+)",
            fontSize = 12.sp
        )
    }
}


@Composable
private fun Day(
    selected: Boolean? = false,
    day: CalendarDay,
    startDate: LocalDate,
    endDate: LocalDate,
    week: CalendarWeek,
    level: Level,
    onClick: (LocalDate) -> Unit,
) {
    val weekDates = week.days.map { it.date }
    if (day.date in startDate..endDate) {
        LevelBox(level.color, selected) {
            onClick(day.date)
        }
    } else if (weekDates.contains(startDate)) {
        LevelBox(Color.Transparent)
    }
}

@Composable
private fun LevelBox(
    color: Color,
    selected: Boolean? = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(daySize)
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .let { modifier ->
                if (selected == true) {
                    modifier.border(1.dp, MaterialTheme.colorScheme.onSurface)
                } else {
                    modifier
                }
            }
            .background(color = color)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
    )
}

private val daySize = 20.dp

@Composable
private fun WeekHeader(dayOfWeek: DayOfWeek) {
    Box(
        modifier = Modifier.height(daySize)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 4.dp),
            text = dayOfWeek.displayText(),
            fontSize = 14.sp,
        )
    }

}

@Composable
private fun MonthHeader(
    calendarMonth: CalendarMonth,
    endDate: LocalDate,
    state: HeatMapCalState,
) {
    val density = LocalDensity.current
    val firstFullyVisibleMonth by remember {
        derivedStateOf { getMonthWithYear(state.layoutInfo, daySize, density) }
    }
    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val title = if (month.month == Month.JANUARY || month == firstFullyVisibleMonth) {
            month.displayText(short = true)
        } else {
            month.month.displayText()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            Text(text = title, fontSize = 12.sp)
        }
    }
}

private fun getMonthWithYear(
    layoutInfo: CalendarLayoutInfo,
    daySize: Dp,
    density: Density,
): YearMonth? {
    val visibleItemsInfo = layoutInfo.visibleMonthsInfo
    return when {
        visibleItemsInfo.isEmpty() -> null
        visibleItemsInfo.count() == 1 -> visibleItemsInfo.first().month.yearMonth
        else -> {
            val firstItem = visibleItemsInfo.first()
            val daySizePx = with(density) { daySize.toPx() }
            if (
                firstItem.size < daySizePx * 4 ||
                firstItem.offset < layoutInfo.viewportStartOffset &&
                (layoutInfo.viewportStartOffset - firstItem.offset > daySizePx)
            ) {
                visibleItemsInfo[1].month.yearMonth
            } else {
                firstItem.month.yearMonth
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onClickBack: () -> Unit,
    onClickSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.nav_statistics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back_24px),
                    contentDescription = "back"
                )
            }
        },
        /*actions = {
            IconButton(
                onClick = onClickSettings
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_settings_24px),
                    contentDescription = "settings")
            }
        },*/
        scrollBehavior = scrollBehavior,
    )
}