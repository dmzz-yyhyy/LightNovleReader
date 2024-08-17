package indi.dmzz_yyhyy.lightnovelreader.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

interface UpdateDialogUiState {
    val visible: Boolean
    val versionName: String
    val releaseNotes: String
    val downloadUrl: String
}

class MutableUpdateDialogUiState: UpdateDialogUiState {
    override var visible by mutableStateOf(false)
    override var versionName by mutableStateOf("")
    override var releaseNotes by mutableStateOf("")
    override var downloadUrl by mutableStateOf("")
}

@HiltViewModel
class LightNovelReaderViewModel @Inject constructor(
    private val updateCheckRepository: UpdateCheckRepository
) : ViewModel() {
    private val _uiSate = MutableUpdateDialogUiState()
    val uiState = _uiSate

    fun checkUpdates() {
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
    }

    fun installUpdate(url: String) {
        /* TODO */
    }
}