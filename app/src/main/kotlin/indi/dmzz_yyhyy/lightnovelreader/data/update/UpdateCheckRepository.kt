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
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@Singleton
class UpdateCheckRepository @Inject constructor(
    userDataDao: UserDataDao
) {
    private val gson: Gson = createGson()
    private val releaseUrl: String = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/bfcd55aa-302c-452a-b59e-90f065d437f5/releases/latest"
    private val developmentUrl: String = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/f21a594f-5a56-4b2b-9361-9a734c10f1c9/releases/latest"
    private val updateChannel = StringUserData(UserDataPath.Settings.App.UpdateChannel.path, userDataDao)

    fun checkAppCenter(): Release {
        val channel = updateChannel.getOrDefault("Development")
        val url = when (channel) {
            "Release" -> releaseUrl
            "Development" -> developmentUrl
            else -> developmentUrl
        }

        Log.i("UpdateChecker", "Checking for updates on channel \"${channel}\"")

        try {
            val response = Jsoup
                .connect(url)
                .ignoreContentType(true)
                .get()
                .body()
                .text()

            val gsonData = gson.fromJson(response, AppCenterMetadata::class.java)
            val available = gsonData.version.toInt() > BuildConfig.VERSION_CODE
            if (available) {
                Log.i("UpdateChecker", "New version available: ${gsonData.versionName}")
                return Release(
                    ReleaseStatus.AVAILABLE,
                    gsonData.version.toInt(),
                    gsonData.versionName,
                    gsonData.releaseNotes,
                    gsonData.downloadUrl,
                    gsonData.downloadSize,
                    gsonData.checksum
                )
            } else {
                Log.i("UpdateChecker", "App is up to date")
                return Release(ReleaseStatus.LATEST)
            }
        } catch (e: Exception) {
            Log.e("UpdateChecker", "Failed to check updates:")
            e.printStackTrace()
            return Release(ReleaseStatus.NULL)
        }
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
            .create()
    }

}