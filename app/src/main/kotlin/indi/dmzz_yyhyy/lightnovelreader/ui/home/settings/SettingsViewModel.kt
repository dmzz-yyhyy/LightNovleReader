package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    var settingState: SettingState? by mutableStateOf(null)

    init {
        if (settingState == null)
            viewModelScope.launch(Dispatchers.IO) {
                settingState = SettingState(userDataRepository, viewModelScope)
            }
    }
}
