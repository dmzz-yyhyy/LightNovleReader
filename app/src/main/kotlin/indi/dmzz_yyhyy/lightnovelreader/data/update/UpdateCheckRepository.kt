package indi.dmzz_yyhyy.lightnovelreader.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ketch.Ketch
import com.ketch.NotificationConfig
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.UserDataDao
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.StringUserData
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

val GITHUB_VERSION_REGEX = """versionCode = (\d{1,3}_\d{1,3}_\d{1,3}_\d{1,3})""".toRegex()
const val GITHUB_BUILD_URL = "https://raw.githubusercontent.com/dmzz-yyhyy/LightNovelReader/refs/tags/%TAG%/app/build.gradle.kts"
val dateFormat = SimpleDateFormat("HH:mm", Locale.US)

enum class Channel(val url: String) {
    APP_CENTER_RELEASE("https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/bfcd55aa-302c-452a-b59e-90f065d437f5/releases/latest"),
    APP_CENTER_DEV("https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/f21a594f-5a56-4b2b-9361-9a734c10f1c9/releases/latest"),

    GITHUB_RELEASE("https://api.github.com/repos/dmzz-yyhyy/LightNovelReader/releases/latest"),
    GITHUB_DEV("https://api.github.com/repos/dmzz-yyhyy/LightNovelReader/releases");

    companion object {
        fun getUrl(channelType: String, sourceType: String): String {
            return when (sourceType) {
                "AppCenter" -> when (channelType) {
                    "Release" -> APP_CENTER_RELEASE.url
                    "Development" -> APP_CENTER_DEV.url
                    else -> APP_CENTER_DEV.url
                }
                "GitHub" -> when (channelType) {
                    "Release" -> GITHUB_RELEASE.url
                    "Development" -> GITHUB_DEV.url
                    else -> GITHUB_DEV.url
                }
                else -> APP_CENTER_DEV.url
            }
        }
    }
}

@Singleton
class UpdateCheckRepository @Inject constructor(
    userDataDao: UserDataDao
) {
    private val gson: Gson = createGson()
    private val updateChannel = StringUserData(UserDataPath.Settings.App.UpdateChannel.path, userDataDao)
    private val distributionPlatform = StringUserData(UserDataPath.Settings.App.DistributionPlatform.path, userDataDao)
    private val githubProxyUrl = StringUserData(UserDataPath.Settings.App.ProxyUrl.path, userDataDao)

    companion object {
        private val _updatePhase = MutableStateFlow("未检查")
        val updatePhase: StateFlow<String> get() = _updatePhase
        val proxyUrlRegex = Regex("(https?://)+[a-zA-Z0-9.-]+(\\.[a-zA-Z]{2,})(/)")
    }
    fun checkUpdates(): Release {
        val channel = updateChannel.getOrDefault("Development")
        val platform = distributionPlatform.getOrDefault("AppCenter")
        val proxyUrl = githubProxyUrl.getOrDefault("").ifBlank { "" }.trim()

        val url = Channel.getUrl(channel, platform)
        _updatePhase.value = "已请求更新，等待 $platform 应答"

        Log.i("UpdateChecker", "Checking for updates on $platform -> $channel")

        try {

            val response = Jsoup
                .connect(url)
                .ignoreContentType(true)
                .get()
                .body()
                .text()

            _updatePhase.value = "检查更新中"
            val gsonData: ReleaseMetadata = when (platform) {
                "AppCenter" -> gson.fromJson(response, AppCenterMetadata::class.java)
                "GitHub" -> {
                    if (channel == "Release") {
                        gson.fromJson(response, GitHubReleaseMetadata::class.java)
                    } else {
                        gson.fromJson(response, GitHubDevMetadata::class.java)
                    }
                }
                else -> throw IllegalArgumentException("Unknown platform type $platform")
            }


            val available = when (platform) {
                "AppCenter" -> (gsonData as AppCenterMetadata).version.toInt() > BuildConfig.VERSION_CODE
                "GitHub" -> {
                    _updatePhase.value = "GitHub 步骤: 提取分支版本"
                    if (proxyUrl.isNotEmpty() && !proxyUrlRegex.matches(proxyUrl)) {
                         throw IllegalArgumentException("代理地址不合法")
                    }
                    val build = Jsoup
                        .connect(proxyUrl + GITHUB_BUILD_URL.replace("%TAG%", gsonData.versionName))
                        .ignoreContentType(true)
                        .get()
                        .body()
                        .text()

                    parseFromRegex(GITHUB_VERSION_REGEX, build) > BuildConfig.VERSION_CODE
                }
                else -> false
            }

            return if (available) {
                _updatePhase.value = "${dateFormat.format(Date())} | 有可用更新: ${gsonData.versionName}"
                Release(
                    ReleaseStatus.AVAILABLE,
                    when (platform) {
                        "AppCenter" -> gsonData.version.toInt()
                        "GitHub" -> parseFromVersionName(gsonData.versionName)
                        else -> 0
                    },
                    gsonData.versionName,
                    gsonData.releaseNotes,
                    proxyUrl + gsonData.downloadUrl,
                    gsonData.downloadSize,
                    gsonData.checksum
                )
            } else {
                _updatePhase.value = "${dateFormat.format(Date())} | 已是最新 (远程: ${gsonData.versionName})"
                Release(ReleaseStatus.LATEST)
            }

        } catch (e: Exception) {
            Log.e("UpdateChecker", "Failed to check updates:")
            e.printStackTrace()
            _updatePhase.value = "${dateFormat.format(Date())} | 失败: ${e.javaClass.simpleName}\n${e.message}"
            return Release(ReleaseStatus.NULL)
        }
    }

    /**
     * 根据版本名解析版本号
     * 例如, "1.2.3-beta4" "1.2.3-dev4" "1.2.3.4" 都将被解析为 10203004
     *
     * @param versionName 版本名
     * @return 解析后的版本号 (Int)
     */
    private fun parseFromVersionName(versionName: String): Int {

        val normalizedVersion = versionName.replace("-", ".")
        val parts = normalizedVersion.split(".").mapIndexed { index, part ->
            if (index == 3) {
                val numericPart = part.filter { it.isDigit() }
                numericPart.toIntOrNull() ?: 0
            } else {
                part.toIntOrNull() ?: 0
            }
        }

        val a = parts.getOrElse(0) { 0 }
        val b = parts.getOrElse(1) { 0 }
        val c = parts.getOrElse(2) { 0 }
        val d = parts.getOrElse(3) { 0 }

        return a * 10000000 + b * 100000 + c * 1000 + d
    }

    /**
     * 根据正则解析版本号
     *
     *
     * @param regex 正则表达式
     * @param content 被匹配的内容
     * @return 解析后的版本号 (Int)
     */
    private fun parseFromRegex(regex: Regex, content: String): Int {
        val matchResult = regex.find(content)

        if (matchResult != null) {
            val versionCodeString = matchResult.groupValues[1]

            val parts = versionCodeString.split("_")

            if (parts.size == 4) {
                val a = parts[0].toInt()
                val b = parts[1].toInt()
                val c = parts[2].toInt()
                val d = parts[3].toInt()

                return a * 10000000 + b * 100000 + c * 1000 + d
            }
        }
        return 0
    }

    fun downloadUpdate(url: String, version: String, checksum: String, context: Context) {
        val fileName = "LightNovelReader-update-$version.apk"
        val cacheDir = File(context.cacheDir, "updates")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        val file = File(cacheDir, fileName)

        if (url.isBlank()) return

        if (file.exists()) {
            if (checkMD5sum(file, checksum)) {
                installApk(file, context)
                return
            } else {
                file.delete()
                Toast.makeText(context, "本地文件校验和计算失败，正在重新下载...", Toast.LENGTH_SHORT).show()
            }
        }

        val ketch: Ketch = Ketch.init(
            context = context,
            notificationConfig = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.icon_foreground,
            )
        )
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch(Dispatchers.IO) {
            ketch.download(
                url = url,
                fileName = fileName,
                path = cacheDir.path,
                tag = "Updates",
                onSuccess = {
                    if (checkMD5sum(file, checksum)) {
                        installApk(file, context)
                    } else {
                        file.delete()
                        Toast.makeText(context, "校验和计算失败，请重试", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    Toast.makeText(context, "下载失败，请尝试手动下载", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )
        }
    }

    private fun installApk(file: File, context: Context) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }

    private fun checkMD5sum(file: File, checksum: String): Boolean {
        if (checksum.contains("skip")) return true
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(1024)

        return try {
            FileInputStream(file).use { stream ->
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }

            val result = digest.digest().joinToString("") { "%02x".format(it) }
            Log.i("UpdateChecker", "CheckMD5sum result: ${file.path}\n[file] $result -> $checksum [expected checksum]")

            result.equals(checksum, ignoreCase = true)
        } catch (e: Exception) {
            Log.e("UpdateChecker", "Error checking MD5 sum", e)
            false
        }
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(AppCenterMetadata::class.java, AppCenterMetadataAdapter())
            .registerTypeAdapter(GitHubReleaseMetadata::class.java, GitHubReleaseMetadataAdapter())
            .registerTypeAdapter(GitHubDevMetadata::class.java, GitHubDevMetadataAdapter())
            .create()
    }

}