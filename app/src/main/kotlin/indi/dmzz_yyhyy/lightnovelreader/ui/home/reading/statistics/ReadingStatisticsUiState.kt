package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

interface ReadingStatisticsUiState {
    var isLoading: Boolean
    var selected: Boolean
    var selectedDate: LocalDate
    var statisticsData: Map<LocalDate, Level>
}

class MutableReadingStatisticsUiState : ReadingStatisticsUiState {
    override var isLoading: Boolean by mutableStateOf(false)
    override var selected: Boolean by mutableStateOf(false)
    override var selectedDate: LocalDate by mutableStateOf(LocalDate.now())
    override var statisticsData: Map<LocalDate, Level> by mutableStateOf(emptyMap())
}
