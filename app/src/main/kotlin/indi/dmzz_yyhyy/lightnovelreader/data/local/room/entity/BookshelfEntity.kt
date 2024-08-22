package indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.ListConverter

@TypeConverters(ListConverter::class)
@Entity(tableName = "book_shelf")
data class BookshelfEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    @ColumnInfo(name = "sort_type")
    val sortType: String,
    @ColumnInfo(name = "auto_cache")
    val autoCache: Boolean,
    @ColumnInfo(name = "system_update_reminder")
    val systemUpdateReminder: Boolean,
    @ColumnInfo(name = "all_book_ids")
    val allBookIds: List<Int>,
    @ColumnInfo(name = "fixed_book_ids")
    val fixedBookIds: List<Int>,
    @ColumnInfo(name = "updated_book_ids")
    val updatedBookIds: List<Int>,
)
