package indi.dmzz_yyhyy.lightnovelreader.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookInformationDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.BookVolumesDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.ChapterContentDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.UserDataDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.UserReadingDataDao
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookInformationEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookShelfBookMetadataEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.BookshelfEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.ChapterContentEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.ChapterInformationEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.UserDataEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.UserReadingDataEntity
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.VolumeEntity

@Database(
    entities = [
        BookInformationEntity::class,
        VolumeEntity::class,
        ChapterInformationEntity::class,
        ChapterContentEntity::class,
        UserReadingDataEntity::class,
        UserDataEntity::class,
        BookshelfEntity::class,
        BookShelfBookMetadataEntity::class,
               ],
    version = 8,
    exportSchema = false
)
abstract class LightNovelReaderDatabase : RoomDatabase() {
    abstract fun bookInformationDao(): BookInformationDao
    abstract fun bookVolumesDao(): BookVolumesDao
    abstract fun chapterContentDao(): ChapterContentDao
    abstract fun userReadingDataDao(): UserReadingDataDao
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var INSTANCE: LightNovelReaderDatabase? = null

        fun getInstance(context: Context): LightNovelReaderDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LightNovelReaderDatabase::class.java,
                        "light_novel_reader_database")
                        .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("drop table book_information")
                db.execSQL( "create table book_information (" +
                        "id INTEGER NOT NULL," +
                        "title TEXT NOT NULL, " +
                        "cover_url TEXT NOT NULL, " +
                        "author TEXT NOT NULL, " +
                        "description TEXT NOT NULL, " +
                        "tags TEXT NOT NULL, " +
                        "publishing_house TEXT NOT NULL, " +
                        "word_count INTEGER NOT NULL," +
                        "last_update TEXT NOT NULL, " +
                        "is_complete INTEGER NOT NULL, " +
                        "PRIMARY KEY(id))" );
                db.execSQL("delete from volume")
            }
        }
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL( "create table book_shelf (" +
                        "id INTEGER NOT NULL," +
                        "name TEXT NOT NULL, " +
                        "sort_type TEXT NOT NULL, " +
                        "auto_cache INTEGER NOT NULL, " +
                        "system_update_reminder TEXT NOT NULL, " +
                        "all_book_ids TEXT NOT NULL, " +
                        "fixed_book_ids TEXT NOT NULL," +
                        "updated_book_ids TEXT NOT NULL, " +
                        "PRIMARY KEY(id))"
                )
                db.execSQL( "create table BookShelfBookMetadata (" +
                        "id INTEGER NOT NULL," +
                        "last_updated TEXT NOT NULL, " +
                        "book_shelf_ids TEXT NOT NULL, " +
                        "PRIMARY KEY(id))"
                )
            }
        }
    }
}