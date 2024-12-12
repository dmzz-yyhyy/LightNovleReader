package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.statistics.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReadingStatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
) : ViewModel() {
    private val _uiState = MutableReadingStatisticsUiState()
    val uiState: ReadingStatisticsUiState = _uiState

    private val _startDate = MutableStateFlow(LocalDate.now().minusMonths(6))
    val startDate: StateFlow<LocalDate> = _startDate

    private val _endDate = MutableStateFlow(LocalDate.now())
    val endDate: StateFlow<LocalDate> = _endDate

    private val _threshold = MutableStateFlow(0)
    val threshold: StateFlow<Int> = _threshold

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            val time = System.currentTimeMillis()
            Log.d("AppReadingStatistics", "Refresh started")

            _uiState.isLoading = true

            val startDate = _startDate.value
            val endDate = _endDate.value

            val levelMap = generateLevelMap(startDate, endDate)

            _uiState.statisticsData = levelMap
            _uiState.isLoading = false

            val elapsed = (System.currentTimeMillis() - time) / 1000.0
            Log.d("AppReadingStatistics", "Refresh completed in $elapsed seconds")
        }
    }

    private suspend fun generateLevelMap(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, Level> {
        val allReadingData = mutableListOf<Pair<LocalDate, Int>>()

        var date = startDate
        while (date <= endDate) {
            val readingTime = statisticsRepository.getReadingTimeForDate(date)?.getTotalMinutes() ?: 0
            allReadingData.add(date to readingTime)
            date = date.plusDays(1)
        }

        val readingTimes = allReadingData.map { it.second }
        val thresholds = calculateThresholds(readingTimes)

        return allReadingData.associate { (date, readingTime) ->
            date to calculateLevel(readingTime, thresholds)
        }
    }

    private fun calculateLevel(readingTime: Int, thresholds: List<Int>): Level {
        if (thresholds.all { it == 0 }) {
            return Level.Zero
        }

        return when {
            readingTime >= thresholds[2] -> Level.Four
            readingTime >= thresholds[1] -> Level.Three
            readingTime >= thresholds[0] -> Level.Two
            readingTime > 0 -> Level.One
            else -> Level.Zero
        }
    }


    private fun calculateThresholds(readingTimes: List<Int>): List<Int> {
        val filteredTimes = readingTimes.filter { it != 0 }
        if (filteredTimes.isEmpty()) return listOf(0, 0, 0)

        val sortedTimes = filteredTimes.sorted()
        val size = sortedTimes.size
        val q1 = sortedTimes[(size * 0.25).toInt()]
        val q2 = sortedTimes[(size * 0.50).toInt()]
        val q3 = sortedTimes[(size * 0.75).toInt()]
        Log.d("AppReadingStatistics", "Calculated thresholds for totalTimes: [1] $q1, [2] $q2, [3] $q3")

        _threshold.value = q3
        return listOf(q1, q2, q3)
    }

    fun selectDate(date: LocalDate) {
        _uiState.selectedDate = date
        _uiState.selected = true
    }
}