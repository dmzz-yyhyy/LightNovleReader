package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent

data class ComicChapterComic(
    @SerializedName("chapter_id")
    val chapterId: Int,
    @SerializedName("comic_id")
    val comicId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("page_url")
    val pageUrl: List<String>,
) {
    fun toChapterContent(lastChapterId: Int, nextChapterId: Int): ChapterContent =
        ChapterContent(
            id = chapterId,
            title = title,
            lastChapter = lastChapterId,
            nextChapter = nextChapterId,
            content = pageUrl.joinToString("") { "[image]$it[image]" }
        )
}