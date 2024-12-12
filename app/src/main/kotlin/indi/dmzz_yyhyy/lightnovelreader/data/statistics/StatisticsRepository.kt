package indi.dmzz_yyhyy.lightnovelreader.data.statistics

import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.ReadingStatisticsDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.ReadingStatisticsEntity
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val readingStatisticsDao: ReadingStatisticsDao
) {

    private suspend fun getReadingStatisticsForDate(date: LocalDate): ReadingStatisticsEntity? {
        return readingStatisticsDao.getReadingStatisticsForDate(date)
    }

    suspend fun updateReadingStatistics(
        date: LocalDate,
        count: Count? = null,
        avgSpeed: Int? = null,
        topBookId: Int? = null,
        topBookReadingTime: Int? = null,
        tags: List<String>? = null,
        startReadingTime: LocalTime? = null,
        latestReadingTime: LocalTime? = null
    ) {
        val entity = readingStatisticsDao.getReadingStatisticsForDate(date)
        val oldCount = entity?.readingTimeCount ?: Count()
        val oldTags = entity?.tags ?: emptyList()

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
            avgSpeed = avgSpeed ?: entity.avgSpeed,
            topBookId = topBookId ?: entity.topBookId,
            topBookReadingTime = topBookReadingTime ?: entity.topBookReadingTime,
            tags = tags ?: oldTags,
            startReadingTime = startReadingTime ?: entity.startReadingTime,
            latestReadingTime = latestReadingTime ?: entity.latestReadingTime
        ) ?: ReadingStatisticsEntity(
            date = date,
            readingTimeCount = oldCount,
            avgSpeed = avgSpeed ?: 0,
            topBookId = topBookId ?: 0,
            topBookReadingTime = topBookReadingTime ?: 0,
            tags = tags ?: emptyList(),
            startReadingTime = startReadingTime ?: LocalTime.MIN,
            latestReadingTime = latestReadingTime ?: LocalTime.MIN
        )

        readingStatisticsDao.insertReadingStatistics(newEntity)
    }

    suspend fun getReadingTimeForDate(date: LocalDate): Count? {
        return getReadingStatisticsForDate(date)?.readingTimeCount
    }

    suspend fun getReadingTimeCountForDate(date: LocalDate): Count? {
        return readingStatisticsDao.getReadingTimeCountForDate(date)
    }

    suspend fun getTopBookIdForDate(date: LocalDate): Int? {
        return readingStatisticsDao.getTopBookIdForDate(date)
    }

    suspend fun getTopBookReadingTimeForDate(date: LocalDate): Int? {
        return readingStatisticsDao.getTopBookReadingTimeForDate(date)
    }

    suspend fun getTagsForDate(date: LocalDate): List<String>? {
        return getReadingStatisticsForDate(date)?.tags
    }

    suspend fun getStartReadingTimeForDate(date: LocalDate): LocalTime? {
        return getReadingStatisticsForDate(date)?.startReadingTime
    }

    suspend fun getLatestReadingTimeForDate(date: LocalDate): LocalTime? {
        return getReadingStatisticsForDate(date)?.latestReadingTime
    }
}
