package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import indi.dmzz_yyhyy.lightnovelreader.R
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
                title = stringResource(R.string.settings_dark_theme),
                description = stringResource(R.string.settings_dark_theme_desc),
                options = MenuOptions.DarkModeOptions,
                selectedOptionKey = state.darkModeKey,
                onOptionChange = onDarkModeChanged
            )
            SettingsMenuEntry(
                title = stringResource(R.string.settings_characters_variant),
                description = stringResource(R.string.settings_characters_variant_desc),
                options = MenuOptions.AppLocaleOptions,
                selectedOptionKey = state.appLocaleKey,
                onOptionChange = onLocaleChanged
            )
        }
    }
}