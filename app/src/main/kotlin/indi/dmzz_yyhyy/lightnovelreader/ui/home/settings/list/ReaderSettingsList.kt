package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsState

@Composable
fun ReaderSettingsList(
    state: SettingsState,
) {

    Box(
        modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp )
    ) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

        }
    }
}