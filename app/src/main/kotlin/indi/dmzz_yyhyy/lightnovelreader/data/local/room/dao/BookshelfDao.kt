package indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfBookMetadata
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfBookMetadataEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookshelfDao {

    @Update
    fun updateBookshelfEntity(bookshelfEntity: BookshelfEntity)

    @Insert
    fun createBookshelf(bookshelfEntity: BookshelfEntity)

    @Query("delete from book_shelf where id=:id")
    fun deleteBookshelf(id: Int)

    @Query("select * from book_shelf where id=:id")
    fun getBookShelf(id: Int): BookshelfEntity?

    @Query("select * from book_shelf where id=:id")
    fun getBookShelfFlow(id: Int): Flow<BookshelfEntity?>

    @Query("select * from book_shelf_book_metadata")
    fun getAllBookshelfBookMetadataEntities(): List<BookshelfBookMetadataEntity>

    @Transaction
    fun getAllBookshelfBookMetadata(): List<BookshelfBookMetadata> = getAllBookshelfBookMetadataEntities()
        .map {
            BookshelfBookMetadata(
                it.id,
                it.lastUpdate,
                it.bookShelfIds
            )
        }

    @Query("select * from book_shelf_book_metadata where id=:id")
    fun getBookshelfBookMetadataEntity(id: Int): BookshelfBookMetadataEntity?

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
    fun getBookshelfBookMetadata(id: Int): BookshelfBookMetadata? = getBookshelfBookMetadataEntity(id)?.let {
        BookshelfBookMetadata(
            it.id,
            it.lastUpdate,
            it.bookShelfIds
        )
    }

    @Transaction
    fun addBookshelfMetadata(
        id: Int,
        lastUpdate: String,
        bookshelfIds: List<Int>
    ) {
        getBookshelfBookMetadataEntity(id).let {
            if ( it == null)
                updateBookshelfBookMetadataEntity(id, lastUpdate, bookshelfIds.joinToString(","))
            else
                updateBookshelfBookMetadataEntity(id, lastUpdate, (bookshelfIds + it.bookShelfIds).distinct().joinToString(","))
        }
    }

    @Query("delete from book_shelf_book_metadata where id=:id")
    fun deleteBookshelfBookMetadata(id: Int)

    @Query("select * from book_shelf_book_metadata")
    fun getAllBookshelfBookEntitiesFlow(): Flow<List<BookshelfBookMetadataEntity>>

    @Query("select id from book_shelf_book_metadata")
    fun getAllBookshelfBookIdsFlow(): Flow<List<Int>>
}