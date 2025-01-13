package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsAboutInfoDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingState

@Composable
fun AboutSettingsList(
    settingState: SettingState) {
    val appInfo: String = buildString {
        appendLine(BuildConfig.APPLICATION_ID)
        append("${BuildConfig.VERSION_NAME} [${BuildConfig.VERSION_CODE}] - ")
            .append(if (BuildConfig.DEBUG) "debug" else "release")
    }
    var showAppInfoDialog by remember { mutableStateOf(false) }

    if (showAppInfoDialog) {
        SettingsAboutInfoDialog(onDismissRequest = { showAppInfoDialog = false })
    }

    SettingsClickableEntry(
        iconRes = R.drawable.info_24px,
        title = stringResource(R.string.app_name),
        description = appInfo,
        onClick = { showAppInfoDialog = true },
        option = "查看详细信息"
    )
    SettingsClickableEntry(
        iconRes = R.drawable.group_24px,
        title = stringResource(R.string.settings_communication),
        description = stringResource(R.string.settings_communication_desc),
        openUrl = "https://qm.qq.com/q/Tp80Hf9Oms"
    )
    SettingsClickableEntry(
        iconRes = R.drawable.archive_24px,
        title = stringResource(R.string.settings_github_repo),
        description = stringResource(R.string.settings_github_repo_desc),
        openUrl = "https://github.com/dmzz-yyhyy/LightNovelReader"
    )
    SettingsSwitchEntry(
        title = stringResource(R.string.settings_statistics),
        description = stringResource(R.string.settings_statistics_desc),
        checked = if (BuildConfig.DEBUG) false else settingState.statistics,
        booleanUserData = settingState.statisticsUserData,
        disabled = BuildConfig.DEBUG
    )
}