package indi.dmzz_yyhyy.lightnovelreader.data.update

import com.google.gson.annotations.SerializedName

data class AppMetaData (
    val version: String,
    @SerializedName("short_version")
    val versionName: String,
    @SerializedName("release_notes")
    val releaseNotes: String,
    @SerializedName("download_url")
    val downloadUrl: String
)