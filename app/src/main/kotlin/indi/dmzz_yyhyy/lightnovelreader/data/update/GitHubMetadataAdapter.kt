package indi.dmzz_yyhyy.lightnovelreader.data.update

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class GitHubReleaseMetadataAdapter : TypeAdapter<GitHubReleaseMetadata>() {
    override fun write(writer: JsonWriter, value: GitHubReleaseMetadata?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()

        writer.name("tag_name").value(value.versionName)
        writer.name("body").value(value.releaseNotes)

        writer.name("assets").beginArray()
        for (asset in value.assets) {
            writer.beginObject()
            writer.name("name").value(asset.name)
            writer.name("download_url").value(asset.browserDownloadUrl)
            writer.name("size").value(asset.size)
            writer.name("checksum").nullValue()
            writer.endObject()
        }

        writer.endArray()
        writer.endObject()
    }

    override fun read(reader: JsonReader): GitHubReleaseMetadata {
        var tagName: String? = null
        var body: String? = null
        val assets = mutableListOf<GitHubAsset>()

        reader.beginObject()
        println("DEBUG $reader")
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "tag_name" -> tagName = reader.nextString()
                "body" -> body = reader.nextString()
                "assets" -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val asset = readAsset(reader)
                        assets.add(asset)
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return GitHubReleaseMetadata(
            version = tagName ?: "",
            versionName = tagName ?: "",
            releaseNotes = body ?: "",
            downloadSize = assets.first().size.toString(),
            checksum = null,
            assets = assets,
            downloadUrl = assets.first().browserDownloadUrl,
        )
    }

    private fun readAsset(reader: JsonReader): GitHubAsset {
        var name: String? = null
        var browserDownloadUrl: String? = null
        var size = 0L

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> name = reader.nextString()
                "browser_download_url" -> browserDownloadUrl = reader.nextString()
                "size" -> size = reader.nextLong()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return GitHubAsset(
            name = name ?: "",
            browserDownloadUrl = browserDownloadUrl ?: "",
            size = size,
        )
    }
}

class GitHubDevMetadataAdapter : TypeAdapter<GitHubReleaseMetadata>() {
    override fun write(writer: JsonWriter, value: GitHubReleaseMetadata?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginObject()

        writer.name("tag_name").value(value.versionName)
        writer.name("body").value(value.releaseNotes)

        writer.name("assets").beginArray()
        for (asset in value.assets) {
            writer.beginObject()
            writer.name("name").value(asset.name)
            writer.name("download_url").value(asset.browserDownloadUrl)
            writer.name("size").value(asset.size)
            writer.name("checksum").nullValue()
            writer.endObject()
        }

        writer.endArray()
        writer.endObject()
    }

    override fun read(reader: JsonReader): GitHubReleaseMetadata {
        val releases = mutableListOf<GitHubReleaseMetadata>()

        reader.beginArray()
        while (reader.hasNext()) {
            releases.add(readSingleRelease(reader))
        }
        reader.endArray()

        val prerelease = releases.find { it.assets.isNotEmpty() && it.assets.first().name.contains("/* FIXME */", ignoreCase = true) }
            ?: throw Exception("No prereleases found!")

        return prerelease
    }

    private fun readSingleRelease(reader: JsonReader): GitHubReleaseMetadata {
        var tagName: String? = null
        var body: String? = null
        val assets = mutableListOf<GitHubAsset>()
        var prerelease = false

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "tag_name" -> tagName = reader.nextString()
                "body" -> body = reader.nextString()
                "prerelease" -> prerelease = reader.nextBoolean()
                "assets" -> {
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val asset = readAsset(reader)
                        assets.add(asset)
                    }
                    reader.endArray()
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return GitHubReleaseMetadata(
            version = tagName ?: "",
            versionName = tagName ?: "",
            releaseNotes = body ?: "",
            downloadSize = assets.firstOrNull()?.size.toString(),
            checksum = null,
            assets = assets,
            downloadUrl = assets.firstOrNull()?.browserDownloadUrl ?: ""
        )
    }

    private fun readAsset(reader: JsonReader): GitHubAsset {
        var name: String? = null
        var browserDownloadUrl: String? = null
        var size = 0L

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> name = reader.nextString()
                "browser_download_url" -> browserDownloadUrl = reader.nextString()
                "size" -> size = reader.nextLong()
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return GitHubAsset(
            name = name ?: "",
            browserDownloadUrl = browserDownloadUrl ?: "",
            size = size,
        )
    }
}
