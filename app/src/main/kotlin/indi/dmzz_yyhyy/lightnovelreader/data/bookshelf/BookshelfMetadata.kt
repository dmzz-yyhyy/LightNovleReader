package indi.dmzz_yyhyy.lightnovelreader.data.bookshelf

import java.time.LocalDateTime

data class BookshelfBookMetadata(
    val id: Int,
    val lastUpdate: LocalDateTime,
    val bookShelfIds: List<Int>,
)

