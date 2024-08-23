package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface UpdateDialogUiState {
    val visible: Boolean
    val versionName: String
    val releaseNotes: String
    val downloadUrl: String
    val downloadSize: String
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var versionName by mutableStateOf("")
    override var releaseNotes by mutableStateOf("")
    override var downloadUrl by mutableStateOf("")
    override var downloadSize by mutableStateOf("0")
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
    userDataRepository: UserDataRepository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    private val _updateDialogUiState = MutableUpdateDialogUiState()
    val updateDialogUiState = _updateDialogUiState
    private var addedBookId = -1
    private val _addToBookshelfDialogUiState = MutableAddToBookshelfDialogUiState()
    val addToBookshelfDialogUiState = _addToBookshelfDialogUiState

    fun onDismissUpdateRequest() {
        _updateDialogUiState.visible = false
    }

    fun checkUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!checkUpdateUserData.getOrDefault(true))
                return@launch
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.checkUpdate()
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.isNeedUpdateFlow.collect {
                        _updateDialogUiState.visible = it
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
        }
    }

    fun installUpdate(url: String, version: String, size: Long, context: Context) =
        updateCheckRepository.installUpdate(url, version, size, context)

    fun requestAddBookToBookshelf(bookId: Int) {
        addedBookId = bookId
        _addToBookshelfDialogUiState.visible = true
        _addToBookshelfDialogUiState.selectedBookshelfIds = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            _addToBookshelfDialogUiState.allBookShelf =
                bookshelfRepository.getAllBookshelfIds()
                    .mapNotNull { bookshelfRepository.getBookshelf(it) }
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

    fun addBookToBookshelf() {
        if (_addToBookshelfDialogUiState.selectedBookshelfIds.isEmpty() || addedBookId == -1) return
        viewModelScope.launch(Dispatchers.IO) {
            _addToBookshelfDialogUiState.selectedBookshelfIds.forEach {
                bookshelfRepository.addBookIntoBookShelf(it, addedBookId)
            }
        }
    }
}