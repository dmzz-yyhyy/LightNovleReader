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
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingsState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun DisplaySettingsList(
    state: SettingsState,
    onDarkModeChanged: (String) -> Unit,
    onLocaleChanged: (String) -> Unit
) {
    Box(
        modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp)
    ) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsMenuEntry(
                title = "深色主题",
                description = "选择是否启用深色主题",
                options = MenuOptions.DarkModeOptions,
                selectedOptionKey = state.darkModeKey,
                onOptionChange = onDarkModeChanged
            )
            SettingsMenuEntry(
                title = "字形",
                description = "使用其他语言的汉字变体，重启应用后生效",
                options = MenuOptions.AppLocaleOptions,
                selectedOptionKey = state.appLocaleKey,
                onOptionChange = onLocaleChanged
            )
        }
    }
}