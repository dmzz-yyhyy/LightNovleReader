package indi.dmzz_yyhyy.lightnovelreader.data.bookshelf

import indi.dmzz_yyhyy.lightnovelreader.data.loacltion.room.converter.LocalDataTimeConverter.dateToString
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookInformationDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookshelfDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfEntity
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class BookshelfRepository @Inject constructor(
    private val bookshelfDao: BookshelfDao,
    private val bookInformationDao: BookInformationDao
) {
    fun getAllBookshelfIds(): List<Int> = bookshelfDao.getAllBookshelfIds()

    @Suppress("DuplicatedCode")
    fun getBookshelf(id: Int): Bookshelf? = MutableBookshelf().apply {
        val bookshelfEntity = bookshelfDao.getBookShelf(id) ?: return null
        this.id = id
        this.name = bookshelfEntity.name
        this.sortType = BookshelfSortType.entries.first { it.key == bookshelfEntity.sortType }
        this.autoCache = bookshelfEntity.autoCache
        this.systemUpdateReminder = bookshelfEntity.systemUpdateReminder
        this.allBookIds = bookshelfEntity.allBookIds
        this.pinnedBookIds = bookshelfEntity.pinnedBookIds
        this.updatedBookIds = bookshelfEntity.updatedBookIds
    }

    @Suppress("DuplicatedCode")
    fun getBookshelfFlow(id: Int): Flow<MutableBookshelf?> = bookshelfDao
        .getBookShelfFlow(id)
        .map { bookshelfEntity ->
            bookshelfEntity ?: return@map null
            MutableBookshelf().apply {
                this.id = id
                this.name = bookshelfEntity.name
                this.sortType = BookshelfSortType.entries.first { it.key == bookshelfEntity.sortType }
                this.autoCache = bookshelfEntity.autoCache
                this.systemUpdateReminder = bookshelfEntity.systemUpdateReminder
                this.allBookIds = bookshelfEntity.allBookIds
                this.pinnedBookIds = bookshelfEntity.pinnedBookIds
                this.updatedBookIds = bookshelfEntity.updatedBookIds
            }
        }

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
            pinnedBookIds = emptyList(),
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
        (bookshelf.pinnedBookIds + listOf(bookId)).let {
            addBooksIntoBookShelf(bookShelfId, bookId)
            bookshelfDao.updateBookshelfEntity(
                bookshelf.copy(
                    pinnedBookIds = it.distinct(),
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