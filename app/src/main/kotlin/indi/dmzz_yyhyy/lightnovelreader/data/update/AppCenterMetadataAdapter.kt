package indi.dmzz_yyhyy.lightnovelreader.data.update

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class AppCenterMetadataAdapter : TypeAdapter<AppCenterMetadata>() {
    override fun write(writer: JsonWriter, value: AppCenterMetadata?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        writer.name("version").value(value.version)
        writer.name("short_version").value(value.versionName)
        writer.name("release_notes").value(value.releaseNotes)
        writer.name("download_url").value(value.downloadUrl)
        writer.name("size").value(value.downloadSize)
        writer.endObject()
    }

    override fun read(reader: JsonReader): AppCenterMetadata {
        var version: String? = null
        var versionName: String? = null
        var releaseNotes: String? = null
        var downloadUrl: String? = null
        var downloadSize: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "version" -> version = reader.nextString()
                "short_version" -> versionName = reader.nextString()
                "release_notes" -> releaseNotes = reader.nextString()
                "download_url" -> downloadUrl = reader.nextString()
                "size" -> downloadSize = reader.nextString()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return AppCenterMetadata(
            version!!,
            versionName!!,
            releaseNotes!!,
            downloadUrl!!,
            downloadSize!!
        )
    }
}
