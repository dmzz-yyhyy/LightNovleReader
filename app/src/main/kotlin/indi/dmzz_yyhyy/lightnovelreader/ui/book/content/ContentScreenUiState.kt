package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.data.book.UserReadingData

@Stable
interface ContentScreenUiState {
    val isLoading: Boolean
    val chapterContent: ChapterContent
    val userReadingData: UserReadingData
    val readingProgress: Float get() =
        if (userReadingData.lastReadChapterId == chapterContent.id) userReadingData.lastReadChapterProgress
        else 0f
    val bookVolumes: BookVolumes
}

class MutableContentScreenUiState: ContentScreenUiState {
    override var isLoading by mutableStateOf(true)
    override var chapterContent by mutableStateOf(ChapterContent.empty())
    override var userReadingData by mutableStateOf(UserReadingData.empty())
    override var bookVolumes by mutableStateOf(BookVolumes.empty())
}