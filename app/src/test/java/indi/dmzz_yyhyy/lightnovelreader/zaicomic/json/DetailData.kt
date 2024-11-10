package indi.dmzz_yyhyy.lightnovelreader.zaicomic.json

import com.google.gson.annotations.SerializedName
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.Volume
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.ComicChapter
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.ComicVolume
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.Tag
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class DetailData(
    val id: Int,
    val title: String,
    val direction: Int,
    val islong: Int,
    val cover: String,
    val description: String,
    @SerializedName("last_updatetime")
    val lastUpdateTime: Long,
    @SerializedName("last_update_chapter_name")
    val lastUpdateChapterName: String,
    @SerializedName("last_update_chapter_id")
    val lastUpdateChapterId: Int,
    @SerializedName("types")
    val tags: List<Tag>,
    val status: List<Tag>,
    val authors: List<Tag>,
    @SerializedName("chapters")
    val volumes: List<ComicVolume>,
) {
    fun toBookInformation(): BookInformation =
        BookInformation(
            id = id,
            title = title,
            coverUrl = cover,
            author = try { authors.joinToString(" ") { it.name } } catch (e: NullPointerException) { "" },
            description = description,
            tags = try { tags.map { it.name } } catch (e: NullPointerException) { emptyList() },
            publishingHouse = "",
            wordCount = 0,
            lastUpdated = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(lastUpdateTime),
                TimeZone.getDefault().toZoneId()),
            isComplete = status.any { it.name == "以完结" }
        )

    fun toBookVolumes(): BookVolumes =
        BookVolumes(
            volumes.mapIndexed { id, comicVolume ->
                Volume(
                    volumeId = this.id*1000+id,
                    volumeTitle = comicVolume.title,
                    chapters = comicVolume.data
                        .sortedBy { it.order }
                        .map {
                            ChapterInformation(
                                id = it.id,
                                title = it.title
                            )
                        }
                )
            }
        )
}

data class Tag(
    @SerializedName("tag_id")
    val id: Int,
    @SerializedName("tag_name")
    val name: String
)

data class ComicVolume(
    val title: String,
    val data: List<ComicChapter>
)

data class ComicChapter(
    @SerializedName("chapter_id")
    val id: Int,
    @SerializedName("chapter_title")
    val title: String,
    @SerializedName("chapter_order")
    val order: Int
)
