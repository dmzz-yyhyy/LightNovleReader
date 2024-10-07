package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDateTime

object LocalTimeDataTypeAdapter : TypeAdapter<LocalDateTime>() {
    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        out?.value(value.toString())
    }

    override fun read(`in`: JsonReader?): LocalDateTime {
        return LocalDateTime.parse(`in`?.nextString())
    }
}