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
    private val readingBookListUserData = userDataRepository.intListUserData(UserDataPath.ReadingBooks.path)
    val uiState: ContentScreenUiState = _uiState
    val settingState = SettingState(userDataRepository, viewModelScope)

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
        println("wasdsfafdasfasdf")
        println(totalReadingTime)
        viewModelScope.launch(Dispatchers.IO) {
            bookRepository.updateUserReadingData(bookId) {
                it.copy(
                    lastReadTime = LocalDateTime.now(),
                    totalReadTime = it.totalReadTime + totalReadingTime
                )
            }
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