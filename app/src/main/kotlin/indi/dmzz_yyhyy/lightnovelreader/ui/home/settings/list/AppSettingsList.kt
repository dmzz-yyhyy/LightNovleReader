package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository.Companion.updatePhase
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsClickableEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun AppSettingsList(
    settingState: SettingState,
    checkUpdate: () -> Unit,
    viewModel: SettingsViewModel
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
    SettingsMenuEntry(
        iconRes = R.drawable.outline_explore_24px,
        title = "Distribution Platform",
        description = "For CN users if your connection to GitHub is slow, please consider switching to other platforms",
        options = MenuOptions.UpdatePlatformOptions,
        selectedOptionKey = settingState.distributionPlatformKey,
        onOptionChange = settingState.distributionPlatformKeyUserData::asynchronousSet
    )
    val updatePhase by updatePhase.collectAsState(initial = "")

    SettingsClickableEntry(
        iconRes = R.drawable.deployed_code_update_24px,
        title = stringResource(R.string.settings_get_updates),
        description = stringResource(R.string.settings_get_updates_desc),
        option = updatePhase,
        onClick = { checkUpdate() }
    )
}