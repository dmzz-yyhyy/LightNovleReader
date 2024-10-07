package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.annotations.SerializedName
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfSortType

data class BookshelfData(
    val id: Int,
    val name: String,
    @SerializedName("sort_type")
    val sortType: BookshelfSortType,
    @SerializedName("auto_cache")
    val autoCache: Boolean,
    @SerializedName("system_update_reminder")
    val systemUpdateReminder: Boolean,
    @SerializedName("all_book_ids")
    val allBookIds: List<Int>,
    @SerializedName("pinned_book_ids")
    val pinnedBookIds: List<Int>,
    @SerializedName("updatedBookIds")
    val updatedBookIds: List<Int>,
)

fun Bookshelf.toJsonData(): BookshelfData =
    BookshelfData(
        id = id,
        name = name,
        sortType = sortType,
        autoCache = autoCache,
        systemUpdateReminder = systemUpdateReminder,
        allBookIds = allBookIds,
        pinnedBookIds = pinnedBookIds,
        updatedBookIds = updatedBookIds,
    )