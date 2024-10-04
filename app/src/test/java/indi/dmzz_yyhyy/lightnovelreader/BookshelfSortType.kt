package indi.dmzz_yyhyy.lightnovelreader

enum class BookshelfSortType(val key: String) {
    Default("default"),
    Latest("latest");
    companion object {
        fun map(key: String): BookshelfSortType = BookshelfSortType.entries.firstOrNull { it.key == key } ?: Default
    }
}