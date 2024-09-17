package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.BookRepository
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ContentViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    userDataRepository: UserDataRepository
) : ViewModel() {
    private val _uiState = MutableContentScreenUiState()
    private var _bookId: Int = -1
    private val fontSizeUserData = userDataRepository.floatUserData(UserDataPath.Reader.FontSize.path)
    private val fontLineHeightUserData = userDataRepository.floatUserData(UserDataPath.Reader.FontLineHeight.path)
    private val keepScreenOnUserData = userDataRepository.booleanUserData(UserDataPath.Reader.KeepScreenOn.path)
    private val isUsingFlipPageUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingFlipPage.path)
    private val isUsingClickFlipPageUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingClickFlipPage.path)
    private val isUsingVolumeKeyFlipUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingVolumeKeyFlip.path)
    private val isUsingFlipAnimeUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingFlipAnime.path)
    private val fastChapterChangeUserData = userDataRepository.booleanUserData(UserDataPath.Reader.FastChapterChange.path)
    private val enableBatteryIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableBatteryIndicator.path)
    private val enableTimeIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableTimeIndicator.path)
    private val enableChapterTitleIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableChapterTitleIndicator.path)
    private val enableReadingChapterProgressIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableReadingChapterProgressIndicator.path)
    private val autoPaddingUserData = userDataRepository.booleanUserData(UserDataPath.Reader.AutoPadding.path)
    private val topPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.TopPadding.path)
    private val bottomPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.BottomPadding.path)
    private val leftPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.LeftPadding.path)
    private val rightPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.RightPadding.path)
    private val readingBookListUserData = userDataRepository.intListUserData(UserDataPath.ReadingBooks.path)
    val uiState: ContentScreenUiState = _uiState

    @Suppress("DuplicatedCode")
    fun init(bookId: Int, chapterId: Int) {
        if (bookId != _bookId) {
            viewModelScope.launch {
                val bookVolumes = bookRepository.getBookVolumes(bookId)
                _uiState.bookVolumes = bookVolumes.first()
                viewModelScope.launch(Dispatchers.IO) {
                    bookVolumes.collect {
                        if (it.volumes.isEmpty()) return@collect
                        _uiState.bookVolumes = it
                    }
                }
            }
        }
        _bookId = bookId
        loadChapterContent(bookId, chapterId)
        viewModelScope.launch(Dispatchers.IO) {
            bookRepository.getUserReadingData(bookId).collect {
                _uiState.userReadingData = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.fontSize = fontSizeUserData.getOrDefault(_uiState.fontSize)
            _uiState.fontLineHeight = fontLineHeightUserData.getOrDefault(_uiState.fontLineHeight)
            _uiState.keepScreenOn = keepScreenOnUserData.getOrDefault(_uiState.keepScreenOn)
            _uiState.isUsingFlipPage = isUsingFlipPageUserData.getOrDefault(_uiState.isUsingFlipPage)
            _uiState.isUsingClickFlipPage = isUsingClickFlipPageUserData.getOrDefault(_uiState.isUsingClickFlipPage)
            _uiState.isUsingVolumeKeyFlip = isUsingVolumeKeyFlipUserData.getOrDefault(_uiState.isUsingVolumeKeyFlip)
            _uiState.isUsingFlipAnime = isUsingFlipAnimeUserData.getOrDefault(_uiState.isUsingFlipAnime)
            _uiState.fastChapterChange = fastChapterChangeUserData.getOrDefault(_uiState.fastChapterChange)
            _uiState.enableBatteryIndicator = enableBatteryIndicatorUserData.getOrDefault(_uiState.enableBatteryIndicator)
            _uiState.enableTimeIndicator = enableTimeIndicatorUserData.getOrDefault(_uiState.enableTimeIndicator)
            _uiState.enableChapterTitleIndicator = enableChapterTitleIndicatorUserData.getOrDefault(_uiState.enableChapterTitleIndicator)
            _uiState.enableReadingChapterProgressIndicator = enableReadingChapterProgressIndicatorUserData.getOrDefault(_uiState.enableReadingChapterProgressIndicator)
            _uiState.autoPadding = autoPaddingUserData.getOrDefault(_uiState.autoPadding)
            _uiState.topPadding = topPaddingUserData.getOrDefault(_uiState.topPadding)
            _uiState.bottomPadding = bottomPaddingUserData.getOrDefault(_uiState.bottomPadding)
            _uiState.leftPadding = leftPaddingUserData.getOrDefault(_uiState.leftPadding)
            _uiState.rightPadding = rightPaddingUserData.getOrDefault(_uiState.rightPadding)
        }
    }

    private fun loadChapterContent(bookId: Int, chapterId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val chapterContent = bookRepository.getChapterContent(
                chapterId = chapterId,
                bookId = bookId
            )
            chapterContent.collect { content ->
                if (content.id == -1) return@collect
                _uiState.chapterContent = content
                _uiState.isLoading = _uiState.chapterContent.id == -1
                bookRepository.updateUserReadingData(bookId) {
                    it.copy(
                        lastReadTime = LocalDateTime.now(),
                        lastReadChapterId = chapterId,
                        lastReadChapterTitle = _uiState.chapterContent.title,
                        lastReadChapterProgress = if (it.lastReadChapterId == chapterId) it.lastReadChapterProgress else 0f,
                    )
                }
                if (content.hasNextChapter()) {
                    bookRepository.getChapterContent(
                        chapterId = chapterId,
                        bookId = bookId
                    )
                }
            }
        }
    }

    fun lastChapter() {
        if (!_uiState.chapterContent.hasLastChapter()) return
        _uiState.isLoading = true
        viewModelScope.launch {
            init(
                bookId = _bookId,
                chapterId = _uiState.chapterContent.lastChapter
            )
        }
    }

    fun nextChapter() {
        if (!_uiState.chapterContent.hasNextChapter()) return
        _uiState.isLoading = true
        viewModelScope.launch {
            init(
                bookId = _bookId,
                chapterId = _uiState.chapterContent.nextChapter
            )
        }
    }

    fun changeChapter(chapterId: Int) {
        _uiState.isLoading = true
        viewModelScope.launch {
            init(
                bookId = _bookId,
                chapterId = chapterId
            )
        }
    }

    fun changeChapterReadingProgress(progress: Float) {
        if (progress.isNaN()) return
        viewModelScope.launch(Dispatchers.IO) {
            bookRepository.updateUserReadingData(_bookId) { userReadingData ->
                val readCompletedChapterIds =
                    if (progress > 0.945 && !userReadingData.readCompletedChapterIds.contains(_uiState.chapterContent.id))
                        userReadingData.readCompletedChapterIds + listOf(_uiState.chapterContent.id)
                    else
                        userReadingData.readCompletedChapterIds
                userReadingData.copy(
                    lastReadTime = LocalDateTime.now(),
                    lastReadChapterId = _uiState.chapterContent.id,
                    lastReadChapterProgress = progress,
                    readingProgress = readCompletedChapterIds.size / _uiState.bookVolumes.volumes.sumOf { it.chapters.size }.toFloat(),
                    readCompletedChapterIds = readCompletedChapterIds
                )
            }
        }
    }

    fun updateTotalReadingTime(bookId: Int, totalReadingTime: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bookRepository.updateUserReadingData(bookId) {
                it.copy(
                    lastReadTime = LocalDateTime.now(),
                    totalReadTime = it.totalReadTime + totalReadingTime
                )
            }
        }
    }

    fun changeFontSize(size: Float) {
        _uiState.fontSize = size
    }

    fun changeFontLineHeight(height: Float) {
        _uiState.fontLineHeight = height
    }

    fun saveFontSize() {
        viewModelScope.launch(Dispatchers.IO) {
            fontSizeUserData.set(_uiState.fontSize)
        }
    }

    fun saveFontLineHeight() {
        viewModelScope.launch(Dispatchers.IO) {
            fontLineHeightUserData.set(_uiState.fontLineHeight)
        }
    }

    fun changeKeepScreenOn(keepScreenOn: Boolean) {
        _uiState.keepScreenOn = keepScreenOn
        viewModelScope.launch(Dispatchers.IO) {
            keepScreenOnUserData.set(keepScreenOn)
        }
    }

    fun changeIsUsingFlipPage(isUsingFlipPage: Boolean) {
        _uiState.isUsingFlipPage = isUsingFlipPage
        viewModelScope.launch(Dispatchers.IO) {
            isUsingFlipPageUserData.set(isUsingFlipPage)
        }
    }

    fun changeIsUsingClickFlipPage(isUsingClickFlipPage: Boolean) {
        _uiState.isUsingClickFlipPage = isUsingClickFlipPage
        viewModelScope.launch(Dispatchers.IO) {
            isUsingClickFlipPageUserData.set(isUsingClickFlipPage)
        }
    }

    fun changeIsUsingVolumeKeyFlip(isUsingVolumeKeyFlip: Boolean) {
        _uiState.isUsingVolumeKeyFlip = isUsingVolumeKeyFlip
        viewModelScope.launch(Dispatchers.IO) {
            isUsingVolumeKeyFlipUserData.set(isUsingVolumeKeyFlip)
        }
    }

    fun changeIsUsingFlipAnime(isUsingFlipAnime: Boolean) {
        _uiState.isUsingFlipAnime = isUsingFlipAnime
        viewModelScope.launch(Dispatchers.IO) {
            isUsingFlipAnimeUserData.set(isUsingFlipAnime)
        }
    }

    fun changeFastChapterChange(fastChapterChange: Boolean) {
        _uiState.fastChapterChange = fastChapterChange
        viewModelScope.launch(Dispatchers.IO) {
            fastChapterChangeUserData.set(fastChapterChange)
        }
    }

    fun changeEnableBatteryIndicator(enableBatteryIndicator: Boolean) {
        _uiState.enableBatteryIndicator = enableBatteryIndicator
        viewModelScope.launch(Dispatchers.IO) {
            enableBatteryIndicatorUserData.set(enableBatteryIndicator)
        }
    }

    fun changeEnableTimeIndicator(enableTimeIndicator: Boolean) {
        _uiState.enableTimeIndicator = enableTimeIndicator
        viewModelScope.launch(Dispatchers.IO) {
            enableTimeIndicatorUserData.set(enableTimeIndicator)
        }
    }

    fun changeEnableChapterTitleIndicator(enableChapterTitleIndicator: Boolean) {
        _uiState.enableChapterTitleIndicator = enableChapterTitleIndicator
        viewModelScope.launch(Dispatchers.IO) {
            enableChapterTitleIndicatorUserData.set(enableChapterTitleIndicator)
        }
    }

    fun changeEnableReadingChapterProgressIndicator(enableReadingChapterProgressIndicator: Boolean) {
        _uiState.enableReadingChapterProgressIndicator = enableReadingChapterProgressIndicator
        viewModelScope.launch(Dispatchers.IO) {
            enableReadingChapterProgressIndicatorUserData.set(enableReadingChapterProgressIndicator)
        }
    }

    fun changeAutoPadding(autoPadding: Boolean) {
        _uiState.autoPadding = autoPadding
        viewModelScope.launch(Dispatchers.IO) {
            autoPaddingUserData.set(autoPadding)
        }
    }

    fun changeTopPadding(padding: Float) {
        _uiState.topPadding = padding
    }

    fun saveTopPadding() {
        viewModelScope.launch(Dispatchers.IO) {
            topPaddingUserData.set(_uiState.topPadding)
        }
    }

    fun changeBottomPadding(padding: Float) {
        _uiState.bottomPadding = padding
    }

    fun saveBottomPadding() {
        viewModelScope.launch(Dispatchers.IO) {
            bottomPaddingUserData.set(_uiState.bottomPadding)
        }
    }

    fun changeLeftPadding(padding: Float) {
        _uiState.leftPadding = padding
    }

    fun saveLeftPadding() {
        viewModelScope.launch(Dispatchers.IO) {
            leftPaddingUserData.set(_uiState.leftPadding)
        }
    }

    fun changeRightPadding(padding: Float) {
        _uiState.rightPadding = padding
    }

    fun saveRightPadding() {
        viewModelScope.launch(Dispatchers.IO) {
            rightPaddingUserData.set(_uiState.rightPadding)
        }
    }

    fun addToReadingBook(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            readingBookListUserData.update {
                val newList = it.toMutableList()
                if (it.contains(bookId))
                    newList.remove(bookId)
                newList.add(bookId)
                return@update newList
            }
        }
    }
}