package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsViewModel

@Composable
fun AboutSettingsList(
    state: SettingsState,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val appInfo: String = buildString {
        appendLine(BuildConfig.APPLICATION_ID)
        append(BuildConfig.VERSION_NAME).append(" (").append(BuildConfig.VERSION_CODE).append(")")
    }

    val buildInfo: String = buildString {
        appendLine(stringResource(R.string.info_build_date))
        appendLine(stringResource(R.string.info_build_host))
        append(if (BuildConfig.DEBUG) "DEV (DEBUG)" else "RELEASE")
    }

    Box(
        modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp )
    ) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsClickableEntry(
                title = stringResource(R.string.app_name),
                description = appInfo,
                openUrl = "https://install.appcenter.ms/users/nightfish2009/apps/lightnovelreader/distribution_groups/public"
            )
            SettingsClickableEntry(
                title = "应用构建",
                description = buildInfo,
            )
            SettingsClickableEntry(
                title = "GitHub 仓库",
                description = "请为本项目点个 star",
                openUrl = "https://github.com/dmzz-yyhyy/LightNovelReader"
            )
            SettingsClickableEntry(
                title = "加入讨论",
                description = "加入我们的 QQ 群讨论或反馈",
                openUrl = "https://qm.qq.com/q/Tp80Hf9Oms"
            )
        }
    }
}