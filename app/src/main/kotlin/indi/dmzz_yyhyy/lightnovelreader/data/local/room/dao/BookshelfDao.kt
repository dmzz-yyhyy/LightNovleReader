package indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookShelfBookMetadataEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfEntity

@Dao
interface BookshelfDao {

    @Update
    fun updateBookshelfEntity(bookshelfEntity: BookshelfEntity)

    @Insert
    fun createBookshelf(bookshelfEntity: BookshelfEntity)

    @Query("select * from book_shelf where id=:id")
    fun getBookShelf(id: Int): BookshelfEntity?

    @Query("select * from book_shelf_book_metadata")
    fun getAllBookshelfBookMetadata(): List<BookShelfBookMetadataEntity>

    @Query("select * from book_shelf_book_metadata where id=:id")
    fun getBookshelfBookMetadata(id: Int): BookShelfBookMetadataEntity?

    @Query("replace into book_shelf_book_metadata (id, last_update, book_shelf_ids)" +
            " values (:id, :lastUpdate, :bookshelfIds)")
    fun updateBookshelfBookMetadataEntity(
        id: Int,
        lastUpdate: String,
        bookshelfIds: String,
    )

    @Query("select id from book_shelf")
    fun getAllBookshelfIds(): List<Int>

    @Transaction
    fun addBookshelfMetadata(
        id: Int,
        lastUpdate: String,
        bookshelfIds: List<Int>
    ) {
        getBookshelfBookMetadata(id).let {
            if ( it == null)
                updateBookshelfBookMetadataEntity(id, lastUpdate, bookshelfIds.joinToString(","))
            else
                updateBookshelfBookMetadataEntity(id, lastUpdate, (bookshelfIds + it.bookShelfIds).distinct().joinToString(","))
        }
    }
}