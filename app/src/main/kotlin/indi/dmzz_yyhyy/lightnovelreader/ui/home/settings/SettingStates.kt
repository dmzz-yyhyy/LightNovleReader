package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.setting.AbstractSettingState
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import kotlinx.coroutines.CoroutineScope

@Stable
class SettingState(
    userDataRepository: UserDataRepository,
    coroutineScope: CoroutineScope
) : AbstractSettingState(coroutineScope) {
    val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    val appLocaleKeyUserData = userDataRepository.stringUserData(UserDataPath.Settings.Display.AppLocale.path)
    val statisticsUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.Statistics.path)
    val darkModeKeyUserData = userDataRepository.stringUserData(UserDataPath.Settings.Display.DarkMode.path)
    val dynamicColorsKeyUserData = userDataRepository.booleanUserData(UserDataPath.Settings.Display.DynamicColors.path)
    val updateChannelKeyUserData = userDataRepository.stringUserData(UserDataPath.Settings.App.UpdateChannel.path)
    val distributionPlatformKeyUserData = userDataRepository.stringUserData(UserDataPath.Settings.App.DistributionPlatform.path)
    val proxyUrlUserData = userDataRepository.stringUserData(UserDataPath.Settings.App.ProxyUrl.path)

    val checkUpdate by checkUpdateUserData.asState(true)
    val appLocaleKey by appLocaleKeyUserData.asState("zh-CN")
    val statistics by statisticsUserData.asState(true)
    val darkModeKey by darkModeKeyUserData.asState("FollowSystem")
    val dynamicColorsKey by dynamicColorsKeyUserData.asState(false)
    val updateChannelKey by updateChannelKeyUserData.asState("Development")
    val distributionPlatformKey by distributionPlatformKeyUserData.asState("GitHub")
    val proxyUrlKey by proxyUrlUserData.asState("https://gh-proxy.com/")
}