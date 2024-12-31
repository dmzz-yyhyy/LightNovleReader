package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import androidx.compose.runtime.getValue
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.setting.AbstractSettingState
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions
import kotlinx.coroutines.CoroutineScope

class SettingState(
    userDataRepository: UserDataRepository,
    coroutineScope: CoroutineScope
) : AbstractSettingState(coroutineScope) {
    val fontSizeUserData = userDataRepository.floatUserData(UserDataPath.Reader.FontSize.path)
    val fontLineHeightUserData = userDataRepository.floatUserData(UserDataPath.Reader.FontLineHeight.path)
    val keepScreenOnUserData = userDataRepository.booleanUserData(UserDataPath.Reader.KeepScreenOn.path)
    val isUsingFlipPageUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingFlipPage.path)
    val isUsingClickFlipPageUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingClickFlipPage.path)
    val isUsingVolumeKeyFlipUserData = userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingVolumeKeyFlip.path)
    val flipAnimeUserData = userDataRepository.stringUserData(UserDataPath.Reader.FlipAnime.path)
    val fastChapterChangeUserData = userDataRepository.booleanUserData(UserDataPath.Reader.FastChapterChange.path)
    val enableBatteryIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableBatteryIndicator.path)
    val enableTimeIndicatorUserData = userDataRepository.booleanUserData(UserDataPath.Reader.EnableTimeIndicator.path)
    val enableChapterTitleIndicatorUserData = userDataRepository.booleanUserData(
        UserDataPath.Reader.EnableChapterTitleIndicator.path)
    val enableReadingChapterProgressIndicatorUserData = userDataRepository.booleanUserData(
        UserDataPath.Reader.EnableReadingChapterProgressIndicator.path)
    val autoPaddingUserData = userDataRepository.booleanUserData(UserDataPath.Reader.AutoPadding.path)
    val topPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.TopPadding.path)
    val bottomPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.BottomPadding.path)
    val leftPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.LeftPadding.path)
    val rightPaddingUserData = userDataRepository.floatUserData(UserDataPath.Reader.RightPadding.path)

    val fontSize by fontSizeUserData.safeAsState(14f)
    val fontLineHeight by fontLineHeightUserData.safeAsState(0f)
    val keepScreenOn by keepScreenOnUserData.safeAsState(false)
    val isUsingFlipPage by isUsingFlipPageUserData.safeAsState(false)
    val isUsingClickFlipPage by isUsingClickFlipPageUserData.safeAsState(false)
    val isUsingVolumeKeyFlip by isUsingVolumeKeyFlipUserData.safeAsState(false)
    val flipAnime by flipAnimeUserData.safeAsState(MenuOptions.FlipAnimeOptions.ScrollWithoutShadow)
    val fastChapterChange by fastChapterChangeUserData.safeAsState(false)
    val enableBatteryIndicator by enableBatteryIndicatorUserData.safeAsState(true)
    val enableTimeIndicator by enableTimeIndicatorUserData.safeAsState(true)
    val enableChapterTitleIndicator by enableChapterTitleIndicatorUserData.safeAsState(true)
    val enableReadingChapterProgressIndicator by enableReadingChapterProgressIndicatorUserData.safeAsState(true)
    val autoPadding by autoPaddingUserData.safeAsState(true)
    val topPadding by topPaddingUserData.safeAsState(12f)
    val bottomPadding by bottomPaddingUserData.safeAsState(12f)
    val leftPadding by leftPaddingUserData.safeAsState(16f)
    val rightPadding by rightPaddingUserData.safeAsState(16f)
}