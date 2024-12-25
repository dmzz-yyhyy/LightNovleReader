package indi.dmzz_yyhyy.lightnovelreader.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun withHaptic(action: (() -> Unit)?): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return {
        action?.let {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            it()
        }
    }
}