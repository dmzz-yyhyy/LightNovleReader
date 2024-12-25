package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun AppSettingsList(
    settingState: SettingState,
    checkUpdate: () -> Unit
) {
    SettingsSwitchEntry(
        iconRes = R.drawable.cloud_download_24px,
        title = stringResource(R.string.settings_auto_check_updates),
        description = stringResource(R.string.settings_auto_check_updates_desc),
        checked = settingState.checkUpdate,
        booleanUserData = settingState.checkUpdateUserData
    )
    SettingsMenuEntry(
        iconRes = R.drawable.alt_route_24px,
        title = stringResource(R.string.settings_update_channel),
        description = stringResource(R.string.settings_update_channel_desc),
        options = MenuOptions.UpdateChannelOptions,
        selectedOptionKey = settingState.updateChannelKey,
        onOptionChange = settingState.updateChannelKeyUserData::asynchronousSet
    )
    SettingsClickableEntry(
        iconRes = R.drawable.deployed_code_update_24px,
        title = stringResource(R.string.settings_get_updates),
        description = stringResource(R.string.settings_get_updates_desc),
        onClick = { checkUpdate() }
    )
}