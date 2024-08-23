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
            _uiState.bookMap.clear()
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
                                                    _uiState.bookMap[id] = it
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
}