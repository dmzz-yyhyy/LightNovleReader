package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfSortType

object BookshelfSortTypeTypeAdapter : TypeAdapter<BookshelfSortType>() {
    override fun write(out: JsonWriter?, value: BookshelfSortType?) {
        out?.value(value?.key)
    }

    override fun read(`in`: JsonReader?): BookshelfSortType {
        return `in`?.nextString()?.let { BookshelfSortType.map(it) } ?: BookshelfSortType.Default
    }
}