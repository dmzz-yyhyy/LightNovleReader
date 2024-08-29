package indi.dmzz_yyhyy.lightnovelreader.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ketch.Ketch
import com.ketch.NotificationConfig
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateCheckRepository @Inject constructor(
    val userDataRepository: UserDataRepository
) {
    private val gson: Gson = createGson()
    private val releaseUrl: String = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/bfcd55aa-302c-452a-b59e-90f065d437f5/releases/latest"
    private val developmentUrl: String = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/f21a594f-5a56-4b2b-9361-9a734c10f1c9/releases/latest"
    private val updateChannel = userDataRepository.stringUserData(UserDataPath.Settings.App.UpdateChannel.path)

    fun checkAppCenter(): Release {
        val url = when (updateChannel.getOrDefault("Development")) {
            "Release" -> developmentUrl
            "Development" -> releaseUrl
            else -> developmentUrl
        }

        Log.i("UpdateChecker", "Checking for updates on channel \"${updateChannel.get()}\"")

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
                    gsonData.downloadSize
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

    fun downloadUpdate(url: String, version: String, size: Long, context: Context) {
        val fileName = "LightNovelReader-update-$version.apk"
        val downloadPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val file = File(downloadPath, fileName)

        if (url.isBlank()) return

        if (file.exists()) {
            if (file.length() == size) {
                installApk(file, context)
                return
            } else file.delete()
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
                path = downloadPath,
                tag = "Updates",
                onSuccess = {
                    installApk(file, context)
                },
                onFailure = {
                    Toast.makeText(context, "下载失败，请尝试手动下载", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(context, intent, null)
                }
            )
        }
    }

    private fun installApk(file: File, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri: Uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            setDataAndType(uri, "application/vnd.android.package-archive")
        }
        context.startActivity(intent)
    }

    private fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(AppCenterMetadata::class.java, AppCenterMetadataAdapter())
            .create()
    }
}