package indi.dmzz_yyhyy.lightnovelreader.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ketch.Ketch
import com.ketch.NotificationConfig
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import indi.dmzz_yyhyy.lightnovelreader.utils.autoReconnectionGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateCheckRepository @Inject constructor (
    val userDataRepository: UserDataRepository
) {
    private val releaseUrl = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/bfcd55aa-302c-452a-b59e-90f065d437f5/releases/latest"
    private val developmentUrl = "https://api.appcenter.ms/v0.1/public/sdk/apps/f7743820-f7dc-498f-b31d-ec5032b0d66d/distribution_groups/f21a594f-5a56-4b2b-9361-9a734c10f1c9/releases/latest"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var ketch: Ketch


    val isNeedUpdateFlow = MutableStateFlow(false)
    val versionNameFlow = MutableStateFlow("?")
    val releaseNotesFlow = MutableStateFlow("*正在获取更新内容...*")
    val downloadUrlFlow = MutableStateFlow("")
    val downloadSizeFlow = MutableStateFlow("0")

    fun checkUpdate() {
        coroutineScope.launch(Dispatchers.IO) {
            when (userDataRepository.stringUserData(UserDataPath.Settings.App.UpdateChannel.path)
                .getOrDefault("Release")) {
                "Development" -> developmentUrl
                "Release" -> releaseUrl
                else -> releaseUrl
            }.let { url ->
                Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .autoReconnectionGet()
                    ?.body()
                    ?.text()
                    .let { Gson().fromJson(it, UpdateMetaData::class.java) }
                    .let {
                        isNeedUpdateFlow.update { _ ->
                            it.version.toInt() > BuildConfig.VERSION_CODE
                        }
                        it
                    }
                    .let {
                        versionNameFlow.update { _ ->
                            it.versionName
                        }
                        it
                    }
                    .let {
                        releaseNotesFlow.update { _ ->
                            it.releaseNotes
                        }
                        it
                    }
                    .let {
                        downloadUrlFlow.update { _ ->
                            it.downloadUrl
                        }
                        it
                    }
                    .let {
                        downloadSizeFlow.update { _ ->
                            it.downloadSize
                        }
                    }

            }
        }
    }

    fun installUpdate(url: String, version: String, context: Context) {
        val fileName = "LightNovelReader-$version.apk"
        ketch = Ketch.init(
            context = context,
            notificationConfig = NotificationConfig(
                enabled = true,
                smallIcon = R.drawable.icon_foreground,
            ),
        )
        coroutineScope.launch(Dispatchers.IO) {
            ketch.download(
                url = url,
                fileName = fileName,
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path,
                tag = "Updates",
                onSuccess = {
                    installApk(
                        file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName),
                        context = context
                    )
                }
            )
        }
    }

    private fun installApk(file: File, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri =
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

}