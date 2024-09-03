package indi.dmzz_yyhyy.lightnovelreader.data.update

data class Release(
    val status: ReleaseStatus,
    val version: Int? = null,
    val versionName: String? = null,
    val releaseNotes: String? = null,
    val downloadUrl: String? = null,
    val downloadSize: String? = null,
    val checksum: String? = null
)

enum class ReleaseStatus {
    LATEST,
    AVAILABLE,
    NULL
}