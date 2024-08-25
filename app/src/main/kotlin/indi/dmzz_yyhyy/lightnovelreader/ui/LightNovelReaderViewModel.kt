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
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface UpdateDialogUiState {
    val visible: Boolean
    val versionName: String
    val versionCode: Int
    val releaseNotes: String
    val downloadUrl: String
    val downloadSize: String
    val toast: String
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var versionName by mutableStateOf("")
    override var versionCode by mutableStateOf(0)
    override var releaseNotes by mutableStateOf("")
    override var downloadUrl by mutableStateOf("")
    override var downloadSize by mutableStateOf("0")
    override var toast by mutableStateOf("")
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
    private var needToast = false
    private var addedBookId = -1
    val updateDialogUiState = _updateDialogUiState
    val addToBookshelfDialogUiState = _addToBookshelfDialogUiState

    fun onDismissUpdateRequest() {
        _updateDialogUiState.visible = false
    }

    @Suppress("DuplicatedCode")
    private fun collectFlows() {
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.checkUpdate()
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.isNeedUpdateFlow.collect {
                _updateDialogUiState.visible = it
                if (needToast && !it)
                    _updateDialogUiState.toast = "当前已是最新版本"
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.versionCodeFlow.collect {
                _updateDialogUiState.versionCode = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.versionNameFlow.collect {
                _updateDialogUiState.versionName = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.releaseNotesFlow.collect {
                _updateDialogUiState.releaseNotes = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.downloadUrlFlow.collect {
                _updateDialogUiState.downloadUrl = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.downloadSizeFlow.collect {
                _updateDialogUiState.downloadSize = it
            }
        }
    }

    fun autoCheckUpdate() {
        needToast = false
        viewModelScope.launch(Dispatchers.IO) {
            if (checkUpdateUserData.getOrDefault(true)) updateCheckRepository.checkUpdate()
            collectFlows()
        }
    }

    fun checkUpdate() {
        needToast = true
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.checkUpdate()
            collectFlows()
        }
    }

    fun installUpdate(url: String, version: String, size: Long, context: Context) =
        updateCheckRepository.installUpdate(url, version, size, context)

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
        viewModelScope.launch(Dispatchers.IO) {
            println(
                bookshelfRepository.getAllBookshelfBooksMetadataFlow().first()
                    .joinToString("\n") { "id: ${it.id} bookshelf: ${it.bookShelfIds.joinToString(", ")}" })
            val oldBookShelfIds = bookshelfRepository.getBookshelfBookMetadata(addedBookId)?.bookShelfIds ?: emptyList()
            _addToBookshelfDialogUiState.selectedBookshelfIds.forEach {
                bookshelfRepository.addBookIntoBookShelf(it, addedBookId)
            }
            oldBookShelfIds.filter { !_addToBookshelfDialogUiState.selectedBookshelfIds.contains(it) }.forEach {
                bookshelfRepository.deleteBookFromBookshelf(it, addedBookId)
            }
            println(
                bookshelfRepository.getAllBookshelfBooksMetadataFlow().first()
                    .joinToString("\n") { "id: ${it.id} bookshelf: ${it.bookShelfIds.joinToString(", ")}" })
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