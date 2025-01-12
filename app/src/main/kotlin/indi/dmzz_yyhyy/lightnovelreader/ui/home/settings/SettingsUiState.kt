package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.material.bottomsheet.BottomSheetBehavior.State

@State
interface SettingsUiState {
    val isCheckingUpdate: Boolean
    val checkUpdatePhase: String
}

class MutableSettingsUiState : SettingsUiState {
    override var isCheckingUpdate: Boolean by mutableStateOf(false)
    override var checkUpdatePhase: String by mutableStateOf("")
}