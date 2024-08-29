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
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun AppSettingsList(
    state: SettingsState,
    onAutoUpdateChanged: (Boolean) -> Unit,
    onUpdateChannelChanged: (String) -> Unit,
    checkUpdate: () -> Unit
) {
    Box(
        modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp )
    ) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSwitchEntry(
                title = stringResource(R.string.settings_auto_check_updates),
                description = stringResource(R.string.settings_auto_check_updates_desc),
                checked = state.checkUpdateEnabled,
                onCheckedChange = onAutoUpdateChanged
            )
            SettingsMenuEntry(
                title = stringResource(R.string.settings_update_channel),
                description = stringResource(R.string.settings_update_channel_desc),
                options = MenuOptions.UpdateChannelOptions,
                selectedOptionKey = state.updateChannelKey,
                onOptionChange = onUpdateChannelChanged
            )
            SettingsClickableEntry(
                title = stringResource(R.string.settings_get_updates),
                description = stringResource(R.string.settings_get_updates_desc),
                onClick = { checkUpdate() }
            )
        }
    }
}