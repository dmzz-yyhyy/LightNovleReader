package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
interface SettingsState {
    val checkUpdateEnabled: Boolean
    val statisticsEnabled: Boolean
    val updateChannelKey: String
    val darkModeKey: String
    val appLocaleKey: String
}

class MutableSettingsState: SettingsState {
    override var checkUpdateEnabled: Boolean by mutableStateOf(true)
    override var statisticsEnabled: Boolean by mutableStateOf(true)
    override var updateChannelKey: String by mutableStateOf("Release")
    override var darkModeKey: String by mutableStateOf("FollowSystem")
    override var appLocaleKey: String by mutableStateOf("zh-CN")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val updateCheckRepository: UpdateCheckRepository
) : ViewModel() {

    private val _settingsState = MutableSettingsState()
    private val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    private val appLocaleKey = userDataRepository.stringUserData(UserDataPath.Settings.Display.AppLocale.path)
    private val statisticsUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.Statistics.path)
    private val darkModeKey = userDataRepository.stringUserData(UserDataPath.Settings.Display.DarkMode.path)
    private val updateChannelKey = userDataRepository.stringUserData(UserDataPath.Settings.App.UpdateChannel.path)
    val settingsState: SettingsState = _settingsState

    fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val checkUpdate = checkUpdateUserData.getOrDefault(true)
            val statistics = statisticsUserData.getOrDefault(true)
            val darkModeKey = darkModeKey.getOrDefault("FollowSystem")
            val appLocaleKey = appLocaleKey.getOrDefault("zh-CN")
            val updateChannelKey = updateChannelKey.getOrDefault("Release")
            _settingsState.checkUpdateEnabled = checkUpdate
            _settingsState.statisticsEnabled = statistics
            _settingsState.darkModeKey = darkModeKey
            _settingsState.appLocaleKey = appLocaleKey
            _settingsState.updateChannelKey = updateChannelKey
        }
    }

    fun onAutoUpdateChanged(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path).set(value)
            _settingsState.checkUpdateEnabled = value
        }
    }

    fun onUpdateChannelChanged(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.stringUserData(UserDataPath.Settings.App.UpdateChannel.path).set(value)
            _settingsState.updateChannelKey = value
        }
    }

    fun onStatisticsChanged(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.booleanUserData(UserDataPath.Settings.App.Statistics.path).set(value)
            _settingsState.statisticsEnabled = value
        }
    }

    fun onDarkModeChanged(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.stringUserData(UserDataPath.Settings.Display.DarkMode.path).set(value)
            _settingsState.darkModeKey = value
        }
    }

    fun onAppLocaleChanged(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDataRepository.stringUserData(UserDataPath.Settings.Display.AppLocale.path).set(value)
            _settingsState.appLocaleKey = value
        }
    }

}
