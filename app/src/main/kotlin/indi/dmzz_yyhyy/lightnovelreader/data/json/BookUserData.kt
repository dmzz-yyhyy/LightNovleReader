package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class BookUserData(
    val id: Int,
    @SerializedName("last_read_time")
    val lastReadTime: LocalDateTime,
    @SerializedName("total_read_time")
    val totalReadTime: Int,
    @SerializedName("reading_progress")
    val readingProgress: Float,
    @SerializedName("last_read_chapter_id")
    val lastReadChapterId: Int,
    @SerializedName("last_read_chapter_title")
    val lastReadChapterTitle: String,
    @SerializedName("last_read_chapter_progress")
    val lastReadChapterProgress: Float,
    @SerializedName("read_completed_chapter_ids")
    val readCompletedChapterIds: List<Int>,
)