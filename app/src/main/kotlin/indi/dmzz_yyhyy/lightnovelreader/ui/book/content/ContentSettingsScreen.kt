package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SheetValue.PartiallyExpanded
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSliderEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    viewModel: ContentViewModel,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    settingState: SettingState
) {
    val isEnableIndicator = (viewModel.settingState.enableBatteryIndicator
            || viewModel.settingState.enableTimeIndicator
            || viewModel.settingState.enableReadingChapterProgressIndicator)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 16.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(50.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        var selectedTabIndex by remember { mutableStateOf(0) }

        val animatedProgress by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        val bgFlashColor by animateColorAsState(
            targetValue = if (animatedProgress > 0.5f) MaterialTheme.colorScheme.primary else Color.Transparent,
            animationSpec = tween(2000)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 16.dp)
        ) {
            AnimatedVisibility(
                visible = sheetState.currentValue == Expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_settings_24px),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "设置预览",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (selectedTabIndex == 2) bgFlashColor else MaterialTheme.colorScheme.surface )
                            .height(200.dp)
                            .padding(
                                top = animateDpAsState(
                                    targetValue = if (viewModel.settingState.autoPadding) 12.dp else viewModel.settingState.topPadding.dp,
                                    animationSpec = tween(300)
                                ).value,
                                start = animateDpAsState(
                                    targetValue = if (viewModel.settingState.autoPadding) 16.dp else viewModel.settingState.leftPadding.dp,
                                    animationSpec = tween(300)
                                ).value,
                                end = animateDpAsState(
                                    targetValue = if (viewModel.settingState.autoPadding) 16.dp else viewModel.settingState.rightPadding.dp,
                                    animationSpec = tween(300)
                                ).value,
                                bottom = animateDpAsState(
                                    targetValue = if (viewModel.settingState.autoPadding) 16.dp else viewModel.settingState.bottomPadding.dp,
                                    animationSpec = tween(300)
                                ).value
                            )
                    ) {
                        Box (
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            ContentText(
                                content = viewModel.uiState.chapterContent.content,
                                onClickLastChapter = { },
                                onClickNextChapter = { },
                                fontSize = viewModel.settingState.fontSize.sp,
                                fontLineHeight = viewModel.settingState.fontLineHeight.sp,
                                readingProgress = viewModel.uiState.readingProgress,
                                isUsingFlipPage = viewModel.settingState.isUsingFlipPage,
                                isUsingClickFlip = viewModel.settingState.isUsingClickFlipPage,
                                isUsingVolumeKeyFlip = viewModel.settingState.isUsingVolumeKeyFlip,
                                isUsingFlipAnime = viewModel.settingState.isUsingFlipAnime,
                                onChapterReadingProgressChange = { },
                                paddingValues = PaddingValues( bottom = if (isEnableIndicator) 46.dp else 12.dp),
                                autoPadding = viewModel.settingState.autoPadding,
                                fastChapterChange = viewModel.settingState.fastChapterChange,
                                changeIsImmersive = {}
                            )
                        }
                        Indicator(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    if (viewModel.settingState.autoPadding)
                                        PaddingValues(
                                            bottom = 8.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                    else PaddingValues(
                                        start = viewModel.settingState.leftPadding.dp,
                                        end = viewModel.settingState.rightPadding.dp
                                    )
                                ),
                            enableBatteryIndicator = viewModel.settingState.enableBatteryIndicator,
                            enableTimeIndicator = viewModel.settingState.enableTimeIndicator,
                            enableChapterTitle = viewModel.settingState.enableChapterTitleIndicator,
                            chapterTitle = viewModel.uiState.chapterContent.title,
                            enableReadingChapterProgressIndicator = viewModel.settingState.enableReadingChapterProgressIndicator,
                            readingChapterProgress = 0.33f
                        )

                    }
                }
            }

            AnimatedVisibility(
                visible = sheetState.currentValue == PartiallyExpanded || sheetState.currentValue == Hidden,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    text = "阅读器设置"
                )
            }

            ContentSettings(
                settingState = settingState,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )
        }
    }
}

data class TabItem(val title:String, val iconRes: Int)

@Composable
fun ContentSettings(
    settingState: SettingState,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        TabItem("外观", R.drawable.filled_menu_book_24px),
        TabItem("操作", R.drawable.settings_applications_24px),
        TabItem("边距", R.drawable.aspect_ratio_24px),
        )
    Column {
        TabRow(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).clip(RoundedCornerShape(6.dp)),
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(tab.iconRes),
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = tab.title,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            ) {
            when (selectedTabIndex) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        item {
                            SettingsSliderEntry(
                                title = "阅读器字体大小",
                                unit = "sp",
                                valueRange = 8f..64f,
                                value = settingState.fontSize,
                                floatUserData = settingState.fontSizeUserData
                            )
                        }
                        item {
                            SettingsSliderEntry(
                                title = "阅读器行距大小",
                                unit = "sp",
                                valueRange = 0f..32f,
                                value = settingState.fontLineHeight,
                                floatUserData = settingState.fontLineHeightUserData
                            )
                        }
                        item {
                            SettingsSwitchEntry(
                                title = "屏幕常亮",
                                description = "在阅读页时，总是保持屏幕开启。这将导致耗电量增加",
                                checked = settingState.keepScreenOn,
                                booleanUserData = settingState.keepScreenOnUserData,
                            )
                        }
                        item {
                            SettingsSwitchEntry(
                                title = "电量指示器",
                                description = "在页面左下角显示当前电量。",
                                checked = settingState.enableBatteryIndicator,
                                booleanUserData = settingState.enableBatteryIndicatorUserData,
                            )
                        }
                        item {
                            SettingsSwitchEntry(
                                title = "时间指示器",
                                description = "在页面左下角显示当前时间。",
                                checked = settingState.enableTimeIndicator,
                                booleanUserData = settingState.enableTimeIndicatorUserData,
                            )
                        }
                        item {
                            SettingsSwitchEntry(
                                title = "名称指示器",
                                description = "在页面右下角显示当前阅读章节名称。",
                                checked = settingState.enableChapterTitleIndicator,
                                booleanUserData = settingState.enableChapterTitleIndicatorUserData,
                            )
                        }
                        item {
                            SettingsSwitchEntry(
                                title = "进度指示器",
                                description = "在页面右下角显示当前阅读进度。",
                                checked = settingState.enableReadingChapterProgressIndicator,
                                booleanUserData = settingState.enableReadingChapterProgressIndicatorUserData,
                            )
                        }

                    }
                }

                1 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        item {
                            SettingsSwitchEntry(
                                title = "翻页模式",
                                description = "切换滚动模式为翻页模式",
                                checked = settingState.isUsingFlipPage,
                                booleanUserData = settingState.isUsingFlipPageUserData,
                            )
                        }
                        if (settingState.isUsingFlipPage) {
                            item {
                                SettingsSwitchEntry(
                                    modifier = Modifier.animateItem(),
                                    title = "音量键控制",
                                    description = "使用音量+键切换至上一页，使用音量-键切换至下一页。",
                                    checked = settingState.isUsingVolumeKeyFlip,
                                    booleanUserData = settingState.isUsingVolumeKeyFlipUserData,
                                )
                            }
                        }
                        if (settingState.isUsingFlipPage) {
                            item {
                                SettingsSwitchEntry(
                                    modifier = Modifier.animateItem(),
                                    title = "点击翻页",
                                    description = "使用点击控制翻页，并将呼出菜单变为上下滑动。",
                                    checked = settingState.isUsingClickFlipPage,
                                    booleanUserData = settingState.isUsingClickFlipPageUserData,
                                )
                            }
                        }
                        if (settingState.isUsingFlipPage) {
                            item {
                                SettingsSwitchEntry(
                                    modifier = Modifier.animateItem(),
                                    title = "启用动画",
                                    description = "开启点击翻页或音量键翻页时的动画，如果关闭可以允许你快速的翻页。",
                                    checked = settingState.isUsingFlipAnime,
                                    booleanUserData = settingState.isUsingFlipAnimeUserData,
                                )
                            }
                        }
                        if (settingState.isUsingFlipPage) {
                            item {
                                SettingsSwitchEntry(
                                    modifier = Modifier.animateItem(),
                                    title = "快速切换章节",
                                    description = "开启后，当你在每章尾页或首页翻页时，会自动切换到上一章或下一章。",
                                    checked = settingState.fastChapterChange,
                                    booleanUserData = settingState.fastChapterChangeUserData,
                                )
                            }
                        }
                    }
                }

                2 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp)),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        item {
                            SettingsSwitchEntry(
                                title = "自动获取边距",
                                description = "自动识别手机屏幕的边距，并进行显示适配，如关闭需要手动进行设置。",
                                checked = settingState.autoPadding,
                                booleanUserData = settingState.autoPaddingUserData,
                            )
                        }
                        if (!settingState.autoPadding) {
                            item {
                                SettingsSliderEntry(
                                    title = "上边距",
                                    unit = "dp",
                                    valueRange = 0f..128f,
                                    value = settingState.topPadding,
                                    floatUserData = settingState.topPaddingUserData
                                )
                            }
                        }
                        if (!settingState.autoPadding) {
                            item {
                                SettingsSliderEntry(
                                    title = "下边距",
                                    unit = "dp",
                                    valueRange = 0f..128f,
                                    value = settingState.bottomPadding,
                                    floatUserData = settingState.bottomPaddingUserData
                                )
                            }
                        }
                        if (!settingState.autoPadding) {
                            item {
                                SettingsSliderEntry(
                                    title = "左边距",
                                    unit = "dp",
                                    valueRange = 0f..128f,
                                    value = settingState.leftPadding,
                                    floatUserData = settingState.leftPaddingUserData
                                )
                            }
                        }
                        if (!settingState.autoPadding) {
                            item {
                                SettingsSliderEntry(
                                    title = "右边距",
                                    unit = "dp",
                                    valueRange = 0f..128f,
                                    value = settingState.rightPadding,
                                    floatUserData = settingState.rightPaddingUserData
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}