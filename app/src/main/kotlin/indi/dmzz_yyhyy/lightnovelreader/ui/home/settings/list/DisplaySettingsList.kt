package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsMenuEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.SettingState
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions

@Composable
fun DisplaySettingsList(
    settingState: SettingState
) {
    SettingsMenuEntry(
        iconRes = R.drawable.dark_mode_24px,
        title = stringResource(R.string.settings_dark_theme),
        description = stringResource(R.string.settings_dark_theme_desc),
        options = MenuOptions.DarkModeOptions,
        selectedOptionKey = settingState.darkModeKey,
        onOptionChange = settingState.darkModeKeyUserData::asynchronousSet
    )
    SettingsSwitchEntry(
        iconRes = R.drawable.format_color_fill_24px,
        title = stringResource(R.string.settings_dynamic_colors),
        description = stringResource(R.string.settings_dynamic_colors_desc),
        checked = settingState.dynamicColorsKey,
        booleanUserData = settingState.dynamicColorsKeyUserData,
        disabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    )
    SettingsMenuEntry(
        iconRes = R.drawable.translate_24px,
        title = stringResource(R.string.settings_characters_variant),
        description = stringResource(R.string.settings_characters_variant_desc),
        options = MenuOptions.AppLocaleOptions,
        selectedOptionKey = settingState.appLocaleKey,
        onOptionChange = settingState.appLocaleKeyUserData::asynchronousSet
    )
}