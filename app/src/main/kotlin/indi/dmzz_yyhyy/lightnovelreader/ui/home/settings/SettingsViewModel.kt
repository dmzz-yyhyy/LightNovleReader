package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.local.LocalBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.work.ExportDataWork
import indi.dmzz_yyhyy.lightnovelreader.data.work.ImportDataWork
import indi.dmzz_yyhyy.lightnovelreader.ui.components.ExportContext
import indi.dmzz_yyhyy.lightnovelreader.ui.components.MutableExportContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val workManager: WorkManager,
    private val webBookDataSource: WebBookDataSource,
    private val localBookDataSource: LocalBookDataSource,
    private val bookshelfRepository: BookshelfRepository,
) : ViewModel() {
    val webBookDataSourceId = webBookDataSource.id
    var settingState: SettingState? by mutableStateOf(null)

    init {
        if (settingState == null) {
            viewModelScope.launch(Dispatchers.IO) {
                settingState = SettingState(userDataRepository, viewModelScope)
            }
        }
    }

    @Suppress("DuplicatedCode")
    fun exportAndSendToFile(exportContext: ExportContext, context: Context) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.applicationInfo.processName}.provider",
            File(context.cacheDir, "LightNovelReaderData.lnr")
        )
        val workRequest = OneTimeWorkRequestBuilder<ExportDataWork>()
            .setInputData(
                workDataOf(
                    "uri" to uri.toString(),
                    "exportBookshelf" to exportContext.bookshelf,
                    "exportReadingData" to exportContext.readingData,
                    "exportSetting" to exportContext.settings,
                    "exportBookmark" to exportContext.bookmark,
                )
            )
            .build()
        workManager.enqueueUniqueWork(
            uri.toString(),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        viewModelScope.launch(Dispatchers.IO) {
            workManager.getWorkInfoByIdFlow(workRequest.id).collect {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        ShareCompat.IntentBuilder(context)
                            .setType("application/zip")
                            .setSubject("分享文件")
                            .addStream(uri)
                            .setChooserTitle("分享")
                            .startChooser()
                    }
                    else -> return@collect
                }
            }
        }
    }

    @Suppress("DuplicatedCode")
    fun exportToFile(uri: Uri, exportContext: ExportContext): OneTimeWorkRequest {
        val workRequest = OneTimeWorkRequestBuilder<ExportDataWork>()
            .setInputData(
                workDataOf(
                    "uri" to uri.toString(),
                    "exportBookshelf" to exportContext.bookshelf,
                    "exportReadingData" to exportContext.readingData,
                    "exportSetting" to exportContext.settings,
                    "exportBookmark" to exportContext.bookmark,
                )
            )
            .build()
        workManager.enqueueUniqueWork(
            uri.toString(),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        return workRequest
    }

    fun importFromFile(uri: Uri, ignoreDataIdCheck: Boolean = false): OneTimeWorkRequest {
        val workRequest = OneTimeWorkRequestBuilder<ImportDataWork>()
            .setInputData(
                workDataOf(
                    "uri" to uri.toString(),
                    "ignoreDataIdCheck" to ignoreDataIdCheck
                )
            )
            .build()
        workManager.enqueueUniqueWork(
            uri.toString(),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        return workRequest
    }
    
    fun changeWebSource(webDataSourceId: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileDir = File(context.filesDir, "data")
            val oldUri = File(fileDir, "${webBookDataSource.id}.data.lnr").toUri()
            workManager.getWorkInfoByIdFlow(exportToFile(oldUri, MutableExportContext().apply { settings = false }).id).collect { workInfo ->
                when(workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        localBookDataSource.clear()
                        bookshelfRepository.clear()
                        userDataRepository.remove(UserDataPath.ReadingBooks.path)
                        userDataRepository.remove(UserDataPath.Search.History.path)
                        userDataRepository.intUserData(UserDataPath.Settings.Data.WebDataSourceId.path).set(webDataSourceId)
                        val newFile = File(fileDir, "$webDataSourceId.data.lnr")
                        if (!newFile.exists()) {
                            restartApp()
                            return@collect
                        }
                        workManager.getWorkInfoByIdFlow(importFromFile(newFile.toUri(), true).id).collect {
                            when(it.state) {
                                WorkInfo.State.SUCCEEDED -> { restartApp() }
                                else -> { }
                            }
                        }
                    }
                    else -> return@collect
                }
            }
        }
    }

    private fun restartApp() {
        exitProcess(0)
    }
}