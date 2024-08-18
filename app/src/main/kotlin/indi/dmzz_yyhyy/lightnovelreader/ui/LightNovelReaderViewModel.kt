package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
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

interface UpdateDialogUiState {
    val visible: Boolean
    val versionName: String
    val releaseNotes: String
    val downloadUrl: String
    val downloadSize: String
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var versionName by mutableStateOf("")
    override var releaseNotes by mutableStateOf("")
    override var downloadUrl by mutableStateOf("")
    override var downloadSize by mutableStateOf("0")
}

@HiltViewModel
class LightNovelReaderViewModel @Inject constructor(
    private val updateCheckRepository: UpdateCheckRepository,
    userDataRepository: UserDataRepository
) : ViewModel() {
    private val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    private val _uiSate = MutableUpdateDialogUiState()
    val uiState = _uiSate

    fun onDismissRequest() {
        _uiSate.visible = false
    }

    fun checkUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!checkUpdateUserData.getOrDefault(true))
                return@launch
            else
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.checkUpdate()
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.isNeedUpdateFlow.collect {
                        _uiSate.visible = it
                    }
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.versionNameFlow.collect {
                        _uiSate.versionName = it
                    }
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.releaseNotesFlow.collect {
                        _uiSate.releaseNotes = it
                    }
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.downloadUrlFlow.collect {
                        _uiSate.downloadUrl = it
                    }
                }
                viewModelScope.launch(Dispatchers.IO) {
                    updateCheckRepository.downloadSizeFlow.collect {
                        _uiSate.downloadSize = it
                    }
                }
        }
    }

    fun installUpdate(url: String, version: String, size: Long, context: Context) =
        updateCheckRepository.installUpdate(url, version, size, context)
}