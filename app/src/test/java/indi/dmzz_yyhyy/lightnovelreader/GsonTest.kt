package indi.dmzz_yyhyy.lightnovelreader


import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime

data class AppUserDataJson(
    val id: Int? = null,
    val data: List<AppUserDataContent>
)

data class AppUserDataContent(
    @SerializedName("web_data_source_id")
    val webDataSourceId: Int,
    @SerializedName("book_user_data")
    val bookUserData: List<BookUserData>? = null,
    @SerializedName("book_shelf")
    val bookshelf: List<BookshelfData>? = null,
    @SerializedName("book_shelf_book_metadata")
    val bookShelfBookMetadata: List<BookShelfBookMetadataData>? = null,
    @SerializedName("user_data")
    val userData: List<UserDataData>? = null,
)

data class BookUserData(
    val id: Int,
    @SerializedName("last_read_time")
    val lastReadTime: LocalDateTime,
    @SerializedName("total_read_time")
    val totalReadTime: Int,
    @SerializedName("reading_progress")
    val readingProgress: Float,
    @SerializedName("last_read_chapter_id")
    val lastReadChapterId: Int,
    @SerializedName("last_read_chapter_title")
    val lastReadChapterTitle: String,
    @SerializedName("last_read_chapter_progress")
    val lastReadChapterProgress: Float,
    @SerializedName("read_completed_chapter_ids")
    val readCompletedChapterIds: List<Int>,
)
data class BookshelfData(
    val id: Int,
    val name: String,
    @SerializedName("sort_type")
    private val sortType: BookshelfSortType,
    @SerializedName("auto_cache")
    val autoCache: Boolean,
    @SerializedName("system_update_reminder")
    val systemUpdateReminder: Boolean,
    @SerializedName("all_book_ids")
    val allBookIds: List<Int>,
    @SerializedName("pinned_book_ids")
    val pinnedBookIds: List<Int>,
    @SerializedName("updatedBookIds")
    val updatedBookIds: List<Int>,
)

data class BookShelfBookMetadataData(
    val id: Int,
    @SerializedName("last_update")
    val lastUpdate: LocalDateTime,
    @SerializedName("book_shelf_ids")
    val bookShelfIds: List<Int>,
)

data class UserDataData(
    val path: String,
    val group: String,
    val type: String,
    val value: String
)

object LocalTimeDataTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        out?.value(value.toString())
    }

    override fun read(`in`: JsonReader?): LocalDateTime {
        return LocalDateTime.parse(`in`?.nextString())
    }
}

object BookshelfSortTypeTypeAdapter : TypeAdapter<BookshelfSortType>() {
    override fun write(out: JsonWriter?, value: BookshelfSortType?) {
        out?.value(value?.key)
    }

    override fun read(`in`: JsonReader?): BookshelfSortType {
        return `in`?.nextString()?.let { BookshelfSortType.map(it) } ?: BookshelfSortType.Default
    }
}

fun main() {
    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalTimeDataTypeAdapter)
        .registerTypeAdapter(BookshelfSortType::class.java, BookshelfSortTypeTypeAdapter)
        .create()
    val data = AppUserDataJson(
        id = -1,
        data = listOf(
            AppUserDataContent(
                webDataSourceId = 1,
                bookUserData = listOf(
                    BookUserData(
                        id = 1,
                        lastReadTime = LocalDateTime.now(),
                        totalReadTime = 0,
                        readingProgress = 0.5f,
                        lastReadChapterId = 1,
                        lastReadChapterTitle = "wasd",
                        lastReadChapterProgress = 0.3f,
                        readCompletedChapterIds = listOf(1),
                    )
                ),
                bookshelf = listOf(
                    BookshelfData(
                        id = 1,
                        name = "",
                        sortType = BookshelfSortType.Default,
                        autoCache = false,
                        systemUpdateReminder = false,
                        allBookIds = listOf(1),
                        pinnedBookIds = listOf(1),
                        updatedBookIds = listOf(1),
                    )
                ),
                bookShelfBookMetadata = listOf(
                    BookShelfBookMetadataData(
                        id = 1,
                        lastUpdate = LocalDateTime.now(),
                        bookShelfIds = listOf(1),
                    )
                ),
                userData = listOf(
                    UserDataData(
                        path = "",
                        group = "",
                        type = "",
                        value = ""
                    )
                )
            )
        )
    )
//    val jsonObject: String = gson.toJson(data)
//    println(jsonObject)
    val jsonObject: String = "{\"data\":[{\"book_shelf\":[{\"all_book_ids\":[],\"auto_cache\":false,\"id\":1728048886,\"name\":\"已收藏\",\"pinned_book_ids\":[],\"sort_type\":\"default\",\"system_update_reminder\":false,\"updatedBookIds\":[]}],\"web_data_source_id\":-791439186}]}87],\"auto_cache\":true,\"id\":1724604519,\"name\":\"已收藏\",\"pinned_book_ids\":[],\"sort_type\":\"default\",\"system_update_reminder\":true,\"updatedBookIds\":[3287]},{\"all_book_ids\":[],\"auto_cache\":false,\"id\":1725115460,\"name\":\"w\",\"pinned_book_ids\":[],\"sort_type\":\"default\",\"system_update_reminder\":false,\"updatedBookIds\":[]},{\"all_book_ids\":[],\"auto_cache\":false,\"id\":1725115464,\"name\":\"w\",\"pinned_book_ids\":[],\"sort_type\":\"default\",\"system_update_reminder\":false,\"updatedBookIds\":[]},{\"all_book_ids\":[],\"auto_cache\":false,\"id\":1728040802,\"name\":\"我1\",\"pinned_book_ids\":[],\"sort_type\":\"default\",\"system_update_reminder\":false,\"updatedBookIds\":[]}],\"web_data_source_id\":-791439186}]}\n"
    println(gson.fromJson(jsonObject, AppUserDataJson::class.java))
}