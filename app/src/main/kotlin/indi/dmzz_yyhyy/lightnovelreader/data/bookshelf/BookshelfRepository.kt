package indi.dmzz_yyhyy.lightnovelreader.data.bookshelf

import indi.dmzz_yyhyy.lightnovelreader.data.loacltion.room.converter.LocalDataTimeConverter.dateToString
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookInformationDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookshelfDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfEntity
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookshelfRepository @Inject constructor(
    private val bookshelfDao: BookshelfDao,
    private val bookInformationDao: BookInformationDao
) {
    fun getAllBookshelfIds(): List<Int> = bookshelfDao.getAllBookshelfIds()

    fun crateBookShelf(
        name: String,
        sortType: BookshelfSortType,
        autoCache: Boolean,
        systemUpdateReminder: Boolean,
    ): Int {
        bookshelfDao.createBookshelf(BookshelfEntity(
            id = name.hashCode(),
            name = name,
            sortType = sortType.key,
            autoCache = autoCache,
            systemUpdateReminder = systemUpdateReminder,
            allBookIds = emptyList(),
            fixedBookIds = emptyList(),
            updatedBookIds = emptyList(),
        ))
        return name.hashCode()
    }

    suspend fun addBooksIntoBookShelf(bookshelfId: Int, bookId: Int) {
        val bookshelf = bookshelfDao.getBookShelf(bookshelfId) ?: return
        bookshelfDao.addBookshelfMetadata(
            id = bookshelfId,
            lastUpdate = dateToString(bookInformationDao.get(bookId)?.lastUpdated ?: LocalDateTime.MIN) ?: "",
            bookshelfIds = listOf(bookshelfId)
        )
        (bookshelf.allBookIds + listOf(bookId)).let {
            bookshelfDao.updateBookshelfEntity(
                bookshelf.copy(
                    allBookIds = it.distinct(),
                )
            )
        }
    }

    suspend fun addFixedBooksIntoBookShelf(bookShelfId: Int, bookId: Int) {
        val bookshelf = bookshelfDao.getBookShelf(bookShelfId) ?: return
        (bookshelf.fixedBookIds + listOf(bookId)).let {
            addBooksIntoBookShelf(bookShelfId, bookId)
            bookshelfDao.updateBookshelfEntity(
                bookshelf.copy(
                    fixedBookIds = it.distinct(),
                )
            )
        }
    }

    suspend fun addUpdatedBooksIntoBookShelf(bookShelfId: Int, bookId: Int) {
        val bookshelf = bookshelfDao.getBookShelf(bookShelfId) ?: return
        (bookshelf.updatedBookIds + listOf(bookId)).let {
            addBooksIntoBookShelf(bookShelfId, bookId)
            bookshelfDao.updateBookshelfEntity(
                bookshelf.copy(
                    updatedBookIds = it.distinct(),
                )
            )
        }
    }
}