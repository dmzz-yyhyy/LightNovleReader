package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.work.ExportDataWork
import indi.dmzz_yyhyy.lightnovelreader.data.work.ImportDataWork
import indi.dmzz_yyhyy.lightnovelreader.ui.components.ExportContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val workManager: WorkManager
) : ViewModel() {
    var settingState: SettingState? by mutableStateOf(null)

    init {
        if (settingState == null)
            viewModelScope.launch(Dispatchers.IO) {
                settingState = SettingState(userDataRepository, viewModelScope)
            }
    }

    @Suppress("DuplicatedCode")
    fun exportAndSendToFile(uri: Uri, exportContext: ExportContext, context: Context) {
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
                        with(context) {
                            val shareIntent = Intent()
                            shareIntent.setAction(Intent.ACTION_SEND)
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri )
                            shareIntent.setType("application/json")
                            startActivity(Intent.createChooser(shareIntent, "分享"))
                        }
                    }
                    else -> return@collect
                }
            }
        }
    }

    @Suppress("DuplicatedCode")
    fun exportToFile(uri: Uri, exportContext: ExportContext) {
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
    }

    fun importFromFile(uri: Uri) {
        val workRequest = OneTimeWorkRequestBuilder<ImportDataWork>()
            .setInputData(
                workDataOf(
                    "uri" to uri.toString()
                )
            )
            .build()
        workManager.enqueueUniqueWork(
            uri.toString(),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}