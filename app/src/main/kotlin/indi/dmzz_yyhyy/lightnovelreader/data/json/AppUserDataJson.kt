package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfSortType
import java.time.LocalDateTime

data class AppUserDataJson(
    @SerializedName("type")
    val type: String,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("data")
    val data: List<AppUserDataContent>
) {
    companion object {
        val gson: Gson = GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .registerTypeAdapter(LocalDateTime::class.java, LocalTimeDataTypeAdapter)
            .registerTypeAdapter(BookshelfSortType::class.java, BookshelfSortTypeTypeAdapter)
            .create()

        fun fromJson(json: String): AppUserDataJson = gson.fromJson(json, AppUserDataJson::class.java)
    }

    fun toJson(): String = gson.toJson(this)
}

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

class AppUserDataJsonBuilder {
    @SerializedName("id")
    private var id: Int? = null
    @SerializedName("data")
    private var data: MutableList<AppUserDataContent> = mutableListOf()

    fun build(): AppUserDataJson = AppUserDataJson(
        type = "light novel reader data file",
        id = id,
        data = data
    )

    fun data(data: AppUserDataContentBuilder.() -> Unit): AppUserDataJsonBuilder {
        this.data.add(
            AppUserDataContentBuilder()
                .let {
                    data.invoke(it)
                    it.build()
                }
        )
        return this
    }
}

class AppUserDataContentBuilder() {
    private var webDataSourceId: Int? = null
    private var bookUserData: MutableList<BookUserData> = mutableListOf()
    private var bookshelf: MutableList<BookshelfData> = mutableListOf()
    private var bookShelfBookMetadata: MutableList<BookShelfBookMetadataData> = mutableListOf()
    private var userData: MutableList<UserDataData> = mutableListOf()

    fun build(): AppUserDataContent {
        if (webDataSourceId == null) {
            throw NullPointerException("webDataSourceId can not be null")
        }
        return AppUserDataContent(
            webDataSourceId = webDataSourceId!!,
            bookUserData = bookUserData.ifEmpty { null },
            bookshelf = bookshelf.ifEmpty { null },
            bookShelfBookMetadata = bookShelfBookMetadata.ifEmpty { null },
            userData = userData.ifEmpty { null },
        )
    }

    fun webDataSourceId(webDataSourceId: Int): AppUserDataContentBuilder {
        this.webDataSourceId = webDataSourceId
        return this
    }

    fun bookUserData(bookUserData: BookUserData): AppUserDataContentBuilder {
        this.bookUserData.add(bookUserData)
        return this
    }

    fun bookshelf(bookshelf: BookshelfData): AppUserDataContentBuilder {
        this.bookshelf.add(bookshelf)
        return this
    }

    fun bookshelfBookMetaData(bookshelfBookMetadata: BookShelfBookMetadataData): AppUserDataContentBuilder {
        this.bookShelfBookMetadata.add(bookshelfBookMetadata)
        return this
    }

    fun userData(userData: UserDataData): AppUserDataContentBuilder {
        this.userData.add(userData)
        return this
    }
}

