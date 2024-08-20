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
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface UpdateDialogUiState {
    val visible: Boolean
    val versionName: String
    val versionCode: Int
    val releaseNotes: String
    val downloadUrl: String
    val downloadSize: String
    val toast: String
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var versionName by mutableStateOf("")
    override var versionCode by mutableStateOf(0)
    override var releaseNotes by mutableStateOf("")
    override var downloadUrl by mutableStateOf("")
    override var downloadSize by mutableStateOf("0")
    override var toast by mutableStateOf("0")
}

@HiltViewModel
class LightNovelReaderViewModel @Inject constructor(
    private val updateCheckRepository: UpdateCheckRepository,
    userDataRepository: UserDataRepository
) : ViewModel() {
    private val checkUpdateUserData = userDataRepository.booleanUserData(UserDataPath.Settings.App.AutoCheckUpdate.path)
    private val _uiState = MutableUpdateDialogUiState()
    private var needToast = false
    val uiState = _uiState

    fun onDismissRequest() {
        _uiState.visible = false
    }

    @Suppress("DuplicatedCode")
    private fun collectFlows() {
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.checkUpdate()
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.isNeedUpdateFlow.collect {
                _uiState.visible = it
                if (needToast && !it)
                    _uiState.toast = "当前已是最新版本"
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.versionCodeFlow.collect {
                _uiState.versionCode = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.versionNameFlow.collect {
                _uiState.versionName = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.releaseNotesFlow.collect {
                _uiState.releaseNotes = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.downloadUrlFlow.collect {
                _uiState.downloadUrl = it
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.downloadSizeFlow.collect {
                _uiState.downloadSize = it
            }
        }
    }

    fun autoCheckUpdate() {
        needToast = false
        viewModelScope.launch(Dispatchers.IO) {
            if (checkUpdateUserData.getOrDefault(true)) updateCheckRepository.checkUpdate()
            collectFlows()
        }
    }

    fun checkUpdate() {
        needToast = true
        viewModelScope.launch(Dispatchers.IO) {
            updateCheckRepository.checkUpdate()
            collectFlows()
        }
    }

    fun installUpdate(url: String, version: String, size: Long, context: Context) =
        updateCheckRepository.installUpdate(url, version, size, context)

    fun clearToast() {
        _uiState.toast = ""
    }
}