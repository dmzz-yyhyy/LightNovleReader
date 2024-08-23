package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.material.bottomsheet.BottomSheetBehavior.State
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.MutableBookshelf

@State
interface BookshelfHomeUiState {
    val bookshelfList: List<Bookshelf>
    val selectedBookshelfId: Int
    val bookMap: Map<Int, BookInformation>
    val selectedTabIndex get() = bookshelfList.indexOfFirst { it.id == selectedBookshelfId }
    val selectedBookshelf: Bookshelf get() = if (selectedTabIndex != -1) bookshelfList[selectedTabIndex] else MutableBookshelf()
}

class MutableBookshelfHomeUiState : BookshelfHomeUiState {
    override var bookshelfList by mutableStateOf(emptyList<MutableBookshelf>())
    override var selectedBookshelfId by mutableStateOf(-1)
    override var bookMap by mutableStateOf(mutableMapOf<Int, BookInformation>())
}
