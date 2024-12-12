package indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.CounterConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.ListConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.LocalDateConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.LocalTimeConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.ReadingStatisticsEntity
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

@Dao
@TypeConverters(LocalDateConverter::class, CounterConverter::class, ListConverter::class, LocalTimeConverter::class)
interface ReadingStatisticsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingStatistics(statistics: ReadingStatisticsEntity)

    @Query("SELECT * FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getReadingStatisticsForDate(date: LocalDate): ReadingStatisticsEntity?

    @Query("SELECT * FROM reading_statistics")
    fun getAllReadingStatistics(): Flow<List<ReadingStatisticsEntity>>

    @Delete
    suspend fun deleteReadingStatistics(statistics: ReadingStatisticsEntity)

    @Query("SELECT readingTimeCount FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getReadingTimeCountForDate(date: LocalDate): Count?

    @Query("SELECT topBookId FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getTopBookIdForDate(date: LocalDate): Int?

    @Query("SELECT topBookReadingTime FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getTopBookReadingTimeForDate(date: LocalDate): Int?

    @Query("SELECT tags FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getTagsForDate(date: LocalDate): List<String>?

    @Query("SELECT startReadingTime FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getStartReadingTimeForDate(date: LocalDate): LocalTime?

    @Query("SELECT latestReadingTime FROM reading_statistics WHERE date = :date LIMIT 1")
    suspend fun getLatestReadingTimeForDate(date: LocalDate): LocalTime?
}
