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
    val fontSize: Float
    val fontLineHeight: Float
    val keepScreenOn: Boolean
    val isUsingFlipPage: Boolean
    val isUsingClickFlipPage: Boolean
    val isUsingVolumeKeyFlip: Boolean
    val isUsingFlipAnime: Boolean
    val enableBatteryIndicator: Boolean
    val enableTimeIndicator: Boolean
    val enableReadingChapterProgressIndicator: Boolean
    val autoPadding: Boolean
    val topPadding: Float
    val bottomPadding: Float
    val leftPadding: Float
    val rightPadding: Float
}

class MutableContentScreenUiState: ContentScreenUiState {
    override var isLoading by mutableStateOf(true)
    override var chapterContent by mutableStateOf(ChapterContent.empty())
    override var userReadingData by mutableStateOf(UserReadingData.empty())
    override var bookVolumes by mutableStateOf(BookVolumes.empty())
    override var fontSize by mutableStateOf(14f)
    override var fontLineHeight by mutableStateOf(0f)
    override var keepScreenOn by mutableStateOf(false)
    override var isUsingFlipPage by mutableStateOf(false)
    override var isUsingClickFlipPage by mutableStateOf(false)
    override var isUsingVolumeKeyFlip by mutableStateOf(false)
    override var isUsingFlipAnime by mutableStateOf(false)
    override var enableBatteryIndicator by mutableStateOf(true)
    override var enableTimeIndicator by mutableStateOf(true)
    override var enableReadingChapterProgressIndicator by mutableStateOf(true)
    override var autoPadding by mutableStateOf(false)
    override var topPadding by mutableStateOf(12f)
    override var bottomPadding by mutableStateOf(12f)
    override var leftPadding by mutableStateOf(16f)
    override var rightPadding by mutableStateOf(16f)
}