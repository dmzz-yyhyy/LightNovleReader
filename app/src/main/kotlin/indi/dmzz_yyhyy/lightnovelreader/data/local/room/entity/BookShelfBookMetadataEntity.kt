package indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import indi.dmzz_yyhyy.lightnovelreader.data.loacltion.room.converter.LocalDataTimeConverter
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter.ListConverter
import java.time.LocalDateTime

@TypeConverters(LocalDataTimeConverter::class, ListConverter::class)
@Entity(tableName = "book_shelf_book_metadata")
data class BookShelfBookMetadataEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "last_update")
    val lastUpdated: LocalDateTime,
    @ColumnInfo(name = "book_shelf_ids")
    val bookShelfIds: List<Int>,
)
