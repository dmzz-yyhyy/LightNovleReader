package indi.dmzz_yyhyy.lightnovelreader.data.bookshelf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.material.bottomsheet.BottomSheetBehavior.State

@State
interface Bookshelf {
    val id: Int
    val name: String
    val sortType: BookshelfSortType
    val autoCache: Boolean
    val systemUpdateReminder: Boolean
    val allBookIds: List<Int>
    val pinnedBookIds: List<Int>
    val updatedBookIds: List<Int>
    fun isEmpty() = this.id == -1
}

class MutableBookshelf : Bookshelf {
    override var id by mutableIntStateOf(-1)
    override var name by mutableStateOf("")
    override var sortType by mutableStateOf(BookshelfSortType.Default)
    override var autoCache by mutableStateOf(false)
    override var systemUpdateReminder by mutableStateOf(false)
    override var allBookIds by mutableStateOf<List<Int>>(listOf())
    override var pinnedBookIds by mutableStateOf<List<Int>>(listOf())
    override var updatedBookIds by mutableStateOf<List<Int>>(listOf())
}
