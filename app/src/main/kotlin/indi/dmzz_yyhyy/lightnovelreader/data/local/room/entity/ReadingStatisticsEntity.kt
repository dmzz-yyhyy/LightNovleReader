package indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.CounterConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.ListConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.LocalDateConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.LocalTimeConverter
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count
import java.time.LocalDate
import java.time.LocalTime

@TypeConverters(LocalDateConverter::class, CounterConverter::class, ListConverter::class, LocalTimeConverter::class)
@Entity(tableName = "reading_statistics")
data class ReadingStatisticsEntity(
    @PrimaryKey
    val date: LocalDate,
    val readingTimeCount: Count,
    val avgSpeed: Int, // avg reading speed (wpm)
    val topBookId: Int,
    val topBookReadingTime: Int,
    val tags: List<String>,
    val startReadingTime: LocalTime,
    val latestReadingTime: LocalTime
)
