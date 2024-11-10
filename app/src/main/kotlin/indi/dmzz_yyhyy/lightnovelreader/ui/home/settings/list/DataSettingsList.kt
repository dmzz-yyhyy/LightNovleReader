package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import indi.dmzz_yyhyy.lightnovelreader.ui.components.ExportContext
import indi.dmzz_yyhyy.lightnovelreader.ui.components.ExportDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.MutableExportContext
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SourceChangeDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.wenku8ApiWebDataSourceItem
import indi.dmzz_yyhyy.lightnovelreader.ui.components.zaiComicWebDataSourceItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingState

@Composable
fun DataSettingsList(
    @Suppress("UNUSED_PARAMETER") settingState: SettingState,
    exportDataToFile: (Uri, ExportContext) -> Unit,
    exportAndSendToFile: (Uri, ExportContext, Context) -> Unit,
    changeWebDataSource: (Int, Context) -> Unit,
    webDataSourceId: Int,
    importData: (Uri) -> Unit,
    dialog: (@Composable () -> Unit) -> Unit,
) {
    var exportContext: ExportContext by remember { mutableStateOf(MutableExportContext()) }
    val context = LocalContext.current
    val saveDataToFileLauncher = launcher {
        exportDataToFile(it, exportContext)
    }
    val saveAndSendDataToFileLauncher = launcher {
        exportAndSendToFile(it, exportContext, context)
    }
    val importDataLauncher = launcher(importData)
    var displayExportDialog by remember { mutableStateOf(false) }
    var displaySourceChangeDialog by remember { mutableStateOf(false) }
    dialog {
        var selectedWebDataSourceId by remember { mutableStateOf(webDataSourceId) }
        if (displaySourceChangeDialog) {
            SourceChangeDialog(
                onDismissRequest = {
                    displaySourceChangeDialog = false
                    selectedWebDataSourceId = webDataSourceId
                },
                onConfirmation = {
                    displaySourceChangeDialog = false
                    changeWebDataSource(selectedWebDataSourceId, context)
                },
                webDataSourceItems = listOf(wenku8ApiWebDataSourceItem, zaiComicWebDataSourceItem),
                selectedWebDataSourceId = selectedWebDataSourceId,
                onClickItem = {
                    selectedWebDataSourceId = it
                }
            )
        }
        if (displayExportDialog) {
            ExportDialog(
                onDismissRequest = { displayExportDialog = false },
                onClickSaveAndSend = {
                    displayExportDialog = false
                    createDataFile("LightNovelReaderData", saveAndSendDataToFileLauncher)
                },
                onClickSaveToFile = {
                    displayExportDialog = false
                    exportContext = it
                    createDataFile("LightNovelReaderData", saveDataToFileLauncher)
                }
            )
        }
    }
    SettingsClickableEntry(
        title = "导出数据",
        description = "将当前应用内的用户数据导出为.lnr文件",
        onClick = { displayExportDialog = true }
    )
    SettingsClickableEntry(
        title = "导入数据",
        description = "从外部.lnr文件内导入数据至软件",
        onClick = { selectDataFile(importDataLauncher) }
    )
    SettingsClickableEntry(
        title = "切换数据源",
        description = "切换软件的网络数据提供源，但这会导致你的用户数据被暂存，将在下次切换到此数据源后恢复。但是你的缓存数据会被永久删除。",
        onClick = { displaySourceChangeDialog = true }
    )
}

@Suppress("DuplicatedCode")
fun createDataFile(fileName: String, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val initUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Documents")
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri)
        putExtra(Intent.EXTRA_TITLE, "$fileName.lnr")
    }
    launcher.launch(Intent.createChooser(intent, "选择一位置"))
}

@Suppress("DuplicatedCode")
fun selectDataFile(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val initUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Documents")
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri)
    }
    launcher.launch(Intent.createChooser(intent, "选择数据文件"))
}

@Composable
fun launcher(block: (Uri) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            activityResult.data?.data?.let { uri ->
                block(uri)
            }
        }
    }
}