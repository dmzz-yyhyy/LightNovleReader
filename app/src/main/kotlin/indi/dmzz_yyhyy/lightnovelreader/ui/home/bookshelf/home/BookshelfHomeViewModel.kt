package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.BookRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.MutableBookshelf
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@HiltViewModel
class BookshelfHomeViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _uiState = MutableBookshelfHomeUiState()
    val uiState: BookshelfHomeUiState = _uiState

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.coroutineContext.cancelChildren()
            _uiState.bookInformationMap.clear()
            _uiState.bookshelfList =
                bookshelfRepository.getAllBookshelfIds()
                    .map { id ->
                        bookshelfRepository.getBookshelfFlow(id)
                            .let {
                                val mutableBookshelf = MutableBookshelf().apply { this.id = id }
                                viewModelScope.launch(Dispatchers.IO) {
                                    it.collect { oldMutableBookshelf ->
                                        oldMutableBookshelf ?: return@collect
                                        mutableBookshelf.id = oldMutableBookshelf.id
                                        mutableBookshelf.name = oldMutableBookshelf.name
                                        mutableBookshelf.sortType = oldMutableBookshelf.sortType
                                        mutableBookshelf.autoCache = oldMutableBookshelf.autoCache
                                        mutableBookshelf.systemUpdateReminder = oldMutableBookshelf.systemUpdateReminder
                                        mutableBookshelf.allBookIds = oldMutableBookshelf.allBookIds
                                        mutableBookshelf.pinnedBookIds = oldMutableBookshelf.pinnedBookIds
                                        mutableBookshelf.updatedBookIds = oldMutableBookshelf.updatedBookIds
                                        oldMutableBookshelf.allBookIds.forEach {
                                            viewModelScope.launch(Dispatchers.IO) {
                                                bookRepository.getBookInformation(it).collect {
                                                    _uiState.bookInformationMap[it.id] = it
                                                }
                                            }
                                        }
                                        oldMutableBookshelf.updatedBookIds.forEach { bookId ->
                                            viewModelScope.launch(Dispatchers.IO) {
                                                bookRepository.getBookVolumes(bookId).collect {
                                                    if (it.volumes.isNotEmpty())
                                                        _uiState.bookLastChapterTitleMap[bookId] = "${it.volumes.last().volumeTitle} ${it.volumes.last().chapters.last().title}"
                                                }
                                            }
                                        }
                                    }
                                }
                                return@let mutableBookshelf
                            }
                    }
            _uiState.bookshelfList.getOrNull(0)?.let {
                changePage(it.id)
            }
        }
    }

    fun changePage(bookshelfId: Int) {
        _uiState.selectedBookshelfId = bookshelfId
    }

    fun enableSelectMode() {
        _uiState.selectMode = true
        _uiState.selectedBookIds.clear()
    }

    fun disableSelectMode() {
        _uiState.selectMode = false
        _uiState.selectedBookIds.clear()
    }

    fun changeBookSelectState(bookId: Int) {
        if (_uiState.selectedBookIds.contains(bookId))
            _uiState.selectedBookIds.remove(bookId)
        else _uiState.selectedBookIds.add(bookId)
    }

    fun selectAllBooks() {
        if (_uiState.selectedBookIds.size == _uiState.selectedBookshelf.allBookIds.size) {
            _uiState.selectedBookIds.clear()
            return
        }
        _uiState.selectedBookIds.clear()
        _uiState.selectedBookIds.addAll(_uiState.selectedBookshelf.allBookIds)
    }

    fun pinSelectedBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val pinnedBookIds = _uiState.selectedBookshelf.pinnedBookIds
            val newPinnedBooksIds = _uiState.selectedBookIds
                .filter { pinnedBookIds.contains(it) }
                .let { removeList ->
                    (pinnedBookIds + _uiState.selectedBookIds).toMutableList().apply {
                        removeAll { removeList.contains(it) }
                    }
                }
            bookshelfRepository.updateBookshelf(_uiState.selectedBookshelfId) {
                it.apply {
                    this.pinnedBookIds = newPinnedBooksIds
                }
            }
        }
    }

    fun removeSelectedBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.selectedBookIds.forEach { bookshelfRepository.deleteBookFromBookshelf(_uiState.selectedBookshelfId, it) }
            _uiState.selectedBookIds.clear()
        }
    }
}