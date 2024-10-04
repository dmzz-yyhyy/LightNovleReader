package indi.dmzz_yyhyy.lightnovelreader.data.bookshelf

enum class BookshelfSortType(val key: String) {
    Default("default"),
    Latest("latest");
    companion object {
        fun map(key: String): BookshelfSortType = BookshelfSortType.entries.first { it.key == key }
    }
}