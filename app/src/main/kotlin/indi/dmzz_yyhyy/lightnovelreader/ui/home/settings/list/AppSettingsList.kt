package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun AppSettingsList(
    state: SettingsState,
    onAutoUpdateChanged: (Boolean) -> Unit,
    onUpdateChannelChanged: (String) -> Unit,
    onCheckUpdateClicked: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp )
    ) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSwitchEntry(
                title = "检查更新",
                description = "软件启动后自动检查更新",
                checked = state.checkUpdateEnabled,
                onCheckedChange = onAutoUpdateChanged
            )
            SettingsMenuEntry(
                title = "更新渠道",
                description = "选择检查更新渠道",
                options = MenuOptions.UpdateChannelOptions,
                selectedOptionKey = state.updateChannelKey,
                onOptionChange = onUpdateChannelChanged
            )
            SettingsClickableEntry(
                title = "检查更新",
                description = "手动检查一次更新",
                option = "asdf",
                onClick = onCheckUpdateClicked
            )
        }
    }
}