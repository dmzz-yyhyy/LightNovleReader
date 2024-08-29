package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.BookRepository
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.update.Release
import indi.dmzz_yyhyy.lightnovelreader.data.update.ReleaseStatus
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

interface UpdateDialogUiState {
    val visible: Boolean
    val toast: String
    val release: Release
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var toast by mutableStateOf("")
    override var release: Release by mutableStateOf(Release(ReleaseStatus.NULL))
}

interface AddToBookshelfDialogUiState {
    val visible: Boolean
    val allBookShelf: List<Bookshelf>
    val selectedBookshelfIds: List<Int>
}

class MutableAddToBookshelfDialogUiState: AddToBookshelfDialogUiState {
    override var visible by mutableStateOf(false)
    override var allBookShelf by mutableStateOf(emptyList<Bookshelf>())
    override var selectedBookshelfIds by mutableStateOf(emptyList<Int>())
}

@HiltViewModel
class LightNovelReaderViewModel @Inject constructor(
    private val updateCheckRepository: UpdateCheckRepository,
    private val bookshelfRepository: BookshelfRepository,
    private val bookRepository: BookRepository,
    userDataRepository: UserDataRepository
) : ViewModel() {
    private val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    private val _updateDialogUiState = MutableUpdateDialogUiState()
    private val _addToBookshelfDialogUiState = MutableAddToBookshelfDialogUiState()
    private var addedBookId = -1
    val updateDialogUiState = _updateDialogUiState
    val addToBookshelfDialogUiState = _addToBookshelfDialogUiState

    fun onDismissUpdateRequest() {
        _updateDialogUiState.visible = false
    }

    fun autoCheckUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            if (checkUpdateUserData.getOrDefault(true)) {
                val release = updateCheckRepository.checkAppCenter()
                when (release.status) {
                    ReleaseStatus.NULL -> return@launch
                    ReleaseStatus.LATEST -> return@launch
                    ReleaseStatus.AVAILABLE -> {
                        _updateDialogUiState.visible = true
                        _updateDialogUiState.release = release
                    }
                }
            }
        }
    }

    fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val release = updateCheckRepository.checkAppCenter()
            when(release.status) {
                ReleaseStatus.NULL -> return@launch
                ReleaseStatus.LATEST -> { _updateDialogUiState.toast = "当前已是最新版本" }
                ReleaseStatus.AVAILABLE -> {
                    _updateDialogUiState.visible = true
                    _updateDialogUiState.release = release
                }
            }
        }
    }

    fun downloadUpdate(url: String, version: String, size: Long, context: Context) =
        updateCheckRepository.downloadUpdate(url, version, size, context)

    fun clearToast() {
        _updateDialogUiState.toast = ""
    }

    fun requestAddBookToBookshelf(bookId: Int) {
        addedBookId = bookId
        _addToBookshelfDialogUiState.visible = true
        viewModelScope.launch(Dispatchers.IO) {
            _addToBookshelfDialogUiState.allBookShelf =
            bookshelfRepository.getAllBookshelfIds()
                .mapNotNull { bookshelfRepository.getBookshelf(it) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _addToBookshelfDialogUiState.selectedBookshelfIds = bookshelfRepository.getBookshelfBookMetadata(bookId)?.bookShelfIds ?: emptyList()
        }
    }

    fun onSelectBookshelf(bookshelfId: Int) {
        if (addedBookId == -1) return
        _addToBookshelfDialogUiState.selectedBookshelfIds += listOf(bookshelfId)
    }

    fun onDeselectBookshelf(bookshelfId: Int) {
        if (addedBookId == -1) return
        _addToBookshelfDialogUiState.selectedBookshelfIds =
            _addToBookshelfDialogUiState.selectedBookshelfIds.toMutableList().apply { removeAll { it == bookshelfId } }
    }

    fun onDismissAddToBookshelfRequest() {
        addedBookId = -1
        _addToBookshelfDialogUiState.visible = false
        _addToBookshelfDialogUiState.selectedBookshelfIds = emptyList()
    }

    fun processAddToBookshelfRequest() {
        _addToBookshelfDialogUiState.visible = false
        if (addedBookId == -1) return
        viewModelScope.launch(Dispatchers.IO) {val oldBookShelfIds = bookshelfRepository.getBookshelfBookMetadata(addedBookId)?.bookShelfIds ?: emptyList()
            bookRepository.getBookInformation(addedBookId).collect { bookInformation ->
                if (bookInformation.isEmpty()) return@collect
                _addToBookshelfDialogUiState.selectedBookshelfIds.forEach {
                    bookshelfRepository.addBookIntoBookShelf(it, bookInformation)
                }
            }
            oldBookShelfIds.filter { !_addToBookshelfDialogUiState.selectedBookshelfIds.contains(it) }.forEach {
                bookshelfRepository.deleteBookFromBookshelf(it, addedBookId)
            }
        }
    }

    fun cacheBook(bookId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val work = bookRepository.cacheBook(bookId)
            bookRepository.isCacheBookWorkFlow(work.id).collect {
                if (it == null) {
                    _updateDialogUiState.toast = "此书本正在缓存中"
                    return@collect
                }
                _updateDialogUiState.toast =
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> "缓存书本完成"
                        WorkInfo.State.FAILED -> "缓存书本失败"
                        WorkInfo.State.RUNNING -> "缓存书本中"
                        else -> ""
                    }
            }
        }
    }
}