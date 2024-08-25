package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.MutableBookshelf
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class EditBookshelfViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val _uiState = MutableBookshelf()
    val uiState: Bookshelf = _uiState

    fun init(id: Int?) {
        id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            (bookshelfRepository.getBookshelf(id) ?: MutableBookshelf()).let {
                this@EditBookshelfViewModel._uiState.id = it.id
                this@EditBookshelfViewModel._uiState.name = it.name
                this@EditBookshelfViewModel._uiState.sortType = it.sortType
                this@EditBookshelfViewModel._uiState.autoCache = it.autoCache
                this@EditBookshelfViewModel._uiState.systemUpdateReminder = it.systemUpdateReminder
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.name = name
    }

    fun onAutoCacheChange(autoCache: Boolean) {
        _uiState.autoCache = autoCache
    }

    fun onSystemUpdateReminderChange(systemUpdateReminder: Boolean) {
        _uiState.systemUpdateReminder = systemUpdateReminder
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.id == -1) {
                bookshelfRepository.crateBookShelf(
                    name = _uiState.name,
                    sortType = _uiState.sortType,
                    autoCache = _uiState.autoCache,
                    systemUpdateReminder = _uiState.systemUpdateReminder
                )
                return@launch
            }
            bookshelfRepository.updateBookshelf(_uiState.id) {
                _uiState.allBookIds = it.allBookIds
                _uiState.pinnedBookIds = it.pinnedBookIds
                _uiState.updatedBookIds = it.updatedBookIds
                _uiState
            }
        }
    }

    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            bookshelfRepository.deleteBookshelf(_uiState.id)
        }
    }
}