package indi.dmzz_yyhyy.lightnovelreader.data.update

interface ReleaseMetadata {
    val version: String
    val versionName: String
    val releaseNotes: String
    val downloadUrl: String
    val downloadSize: String
    val checksum: String?
}

data class AppCenterMetadata(
    override val version: String,
    override val versionName: String,
    override val releaseNotes: String,
    override val downloadUrl: String,
    override val downloadSize: String,
    override val checksum: String
) : ReleaseMetadata

data class GitHubReleaseMetadata(
    override val version: String,
    override val versionName: String,
    override val releaseNotes: String,
    override val downloadUrl: String,
    override val downloadSize: String,
    override val checksum: String,
    val assets: List<GitHubAsset> = listOf()
) : ReleaseMetadata

data class GitHubDevMetadata(
    val prerelease: Boolean,
    override val version: String,
    override val versionName: String,
    override val releaseNotes: String,
    override val downloadUrl: String,
    override val downloadSize: String,
    override val checksum: String,
    val assets: List<GitHubAsset> = listOf()
) : ReleaseMetadata

data class GitHubAsset(
    val name: String,
    val browserDownloadUrl: String,
    val size: Long,
)