package indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.CounterConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.LocalDateConverter
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count
import java.time.LocalDate

@TypeConverters(LocalDateConverter::class, CounterConverter::class)
@Entity(tableName = "reading_statistics")
data class ReadingStatisticsEntity(
    @PrimaryKey
    val date: LocalDate,
    val readingTimeCount: Count,
    val topBookId: Int,
    val topBookReadingTime: Int
)