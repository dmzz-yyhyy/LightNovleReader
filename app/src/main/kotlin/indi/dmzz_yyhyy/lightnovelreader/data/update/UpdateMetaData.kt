package indi.dmzz_yyhyy.lightnovelreader.data.update

import com.google.gson.annotations.SerializedName

data class UpdateMetaData (
    val version: String,
    @SerializedName("short_version")
    val versionName: String,
    @SerializedName("release_notes")
    val releaseNotes: String,
    @SerializedName("download_url")
    val downloadUrl: String,
    @SerializedName("size")
    val downloadSize: String
)