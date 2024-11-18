package indi.dmzz_yyhyy.lightnovelreader.data.statistics

import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.ReadingStatisticsDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.ReadingStatisticsEntity
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val readingStatisticsDao: ReadingStatisticsDao
) {

    private suspend fun getReadingStatisticsForDate(date: LocalDate): ReadingStatisticsEntity? {
        val data = readingStatisticsDao.getReadingStatisticsForDate(date)
        return data
    }

    suspend fun updateReadingStatistics(
        date: LocalDate,
        count: Count? = null,
        topBookId: Int? = null,
        topBookReadingTime: Int? = null
    ) {
        val entity = readingStatisticsDao.getReadingStatisticsForDate(date)
        val oldCount = entity?.readingTimeCount ?: Count()

        count?.let {
            for (hour in 0..23) {
                val newMinuteCount = it.getMinute(hour)
                if (newMinuteCount > 0) {
                    oldCount.setMinute(hour, newMinuteCount)
                }
            }
        }

        val newEntity = entity?.copy(
            readingTimeCount = oldCount,
            topBookId = topBookId ?: entity.topBookId,
            topBookReadingTime = topBookReadingTime ?: entity.topBookReadingTime
        ) ?: ReadingStatisticsEntity(
            date = date,
            readingTimeCount = oldCount,
            topBookId = topBookId ?: 0,
            topBookReadingTime = topBookReadingTime ?: 0
        )

        readingStatisticsDao.insertReadingStatistics(newEntity)
    }

    suspend fun getReadingTimeForDate(date: LocalDate): Count? {
        return getReadingStatisticsForDate(date)?.readingTimeCount
    }

    suspend fun getReadingTimeCountForDate(date: LocalDate): ByteArray? {
        return readingStatisticsDao.getReadingTimeCountForDate(date)
    }

    suspend fun getTopBookIdForDate(date: LocalDate): Int? {
        return readingStatisticsDao.getTopBookIdForDate(date)
    }

    suspend fun getTopBookReadingTimeForDate(date: LocalDate): Int? {
        return readingStatisticsDao.getTopBookReadingTimeForDate(date)
    }
}
