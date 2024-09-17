package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import android.app.Activity
import android.content.Context.BATTERY_SERVICE
import android.os.BatteryManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.ui.components.AnimatedText
import indi.dmzz_yyhyy.lightnovelreader.ui.components.FilledCard
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Loading
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSliderEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSwitchEntry
import java.time.LocalDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    onClickBackButton: () -> Unit,
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    bookId: Int,
    chapterId: Int,
    viewModel: ContentViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val settingsBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val chapterSelectorBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isRunning by remember { mutableStateOf(false) }
    var isImmersive by remember { mutableStateOf(false) }
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    var showChapterSelectorBottomSheet by remember { mutableStateOf(false) }
    var totalReadingTime by remember { mutableStateOf(0) }
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            isImmersive = false
        }
    }

    LaunchedEffect(isImmersive) {
        val window = (context as ComponentActivity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        if (isImmersive) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    topBar {
        AnimatedVisibility(
            visible = !isImmersive,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            TopBar(
                onClickBackButton = onClickBackButton,
                title = viewModel.uiState.chapterContent.title,
                it
            )
        }
    }

    LaunchedEffect(bookId) {
        viewModel.addToReadingBook(bookId)
    }
    LaunchedEffect(chapterId) {
        viewModel.init(bookId, chapterId)
        totalReadingTime = 0
    }
    LifecycleResumeEffect(Unit) {
        isRunning = true
        onPauseOrDispose {
            isRunning = false
            viewModel.updateTotalReadingTime(bookId, totalReadingTime)
        }
    }
    LaunchedEffect(viewModel.uiState.keepScreenOn) {
        if (viewModel.uiState.keepScreenOn)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    LaunchedEffect(isRunning) {
        while (isRunning) {
            totalReadingTime += 1
            delay(1000)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    AnimatedVisibility(
        visible =  viewModel.uiState.isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Loading()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = viewModel.uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Loading()
        }
        AnimatedVisibility(
            visible = !viewModel.uiState.isLoading,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit = fadeOut() + scaleOut(targetScale = 0.7f)
        ) {
            val isEnableIndicator = viewModel.uiState.enableBatteryIndicator || viewModel.uiState.enableTimeIndicator || viewModel.uiState.enableReadingChapterProgressIndicator
            Box(Modifier.fillMaxSize()) {
                AnimatedContent(
                    viewModel.uiState.chapterContent.content,
                    label = "ContentAnimate"
                ) { text ->
                    ContentText(
                        content = text,
                        fontSize = viewModel.uiState.fontSize.sp,
                        fontLineHeight = viewModel.uiState.fontLineHeight.sp,
                        readingProgress = viewModel.uiState.readingProgress,
                        isUsingFlipPage = viewModel.uiState.isUsingFlipPage,
                        isUsingClickFlip = viewModel.uiState.isUsingClickFlipPage,
                        isUsingVolumeKeyFlip = viewModel.uiState.isUsingVolumeKeyFlip,
                        isUsingFlipAnime = viewModel.uiState.isUsingFlipAnime,
                        onChapterReadingProgressChange = viewModel::changeChapterReadingProgress,
                        changeIsImmersive = { isImmersive = !isImmersive },
                        paddingValues =
                        if (viewModel.uiState.autoPadding)
                            PaddingValues(
                                top = 12.dp,
                                bottom = if (isEnableIndicator) 46.dp else 12.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        else PaddingValues(
                            top = viewModel.uiState.topPadding.dp,
                            bottom = if (isEnableIndicator) (viewModel.uiState.bottomPadding + 38).dp else viewModel.uiState.bottomPadding.dp,
                            start = viewModel.uiState.leftPadding.dp,
                            end = viewModel.uiState.rightPadding.dp
                        ),
                        autoPadding = viewModel.uiState.autoPadding
                    )
                }
                AnimatedVisibility (
                    modifier = Modifier.align(Alignment.BottomCenter),
                    visible = isEnableIndicator,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Indicator(
                        Modifier
                            .padding(
                                if (viewModel.uiState.autoPadding)
                                    PaddingValues(
                                        bottom = 8.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    )
                                else PaddingValues(
                                    bottom = viewModel.uiState.bottomPadding.dp,
                                    start = viewModel.uiState.leftPadding.dp,
                                    end = viewModel.uiState.rightPadding.dp
                                )
                            ),
                        enableBatteryIndicator = viewModel.uiState.enableBatteryIndicator,
                        enableTimeIndicator = viewModel.uiState.enableTimeIndicator,
                        enableReadingChapterProgressIndicator = viewModel.uiState.enableReadingChapterProgressIndicator,
                        readingChapterProgress = viewModel.uiState.readingProgress
                    )
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = !isImmersive,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            BottomBar(
                chapterContent = viewModel.uiState.chapterContent,
                readingChapterProgress = viewModel.uiState.readingProgress,
                onClickLastChapter = {
                    viewModel.lastChapter()
                },
                onClickNextChapter = {
                    viewModel.nextChapter()
                },
                onClickSettings = { showSettingsBottomSheet = true },
                onClickChapterSelector = { showChapterSelectorBottomSheet = true },
            )
        }
        AnimatedVisibility(visible = showSettingsBottomSheet) {
            SettingsBottomSheet(
                state = settingsBottomSheetState,
                onDismissRequest = {
                    coroutineScope.launch { settingsBottomSheetState.hide() }.invokeOnCompletion {
                        if (!settingsBottomSheetState.isVisible) {
                            showSettingsBottomSheet = false
                        }
                    }
                    showSettingsBottomSheet = false
                },
                fontSize = viewModel.uiState.fontSize,
                onFontSizeSliderChange = viewModel::changeFontSize,
                onFontSizeSliderChangeFinished = viewModel::saveFontSize,
                fontLineHeight = viewModel.uiState.fontLineHeight,
                onFontLineHeightSliderChange = viewModel::changeFontLineHeight,
                onFontLineHeightSliderChangeFinished = viewModel::saveFontLineHeight,
                isKeepScreenOn = viewModel.uiState.keepScreenOn,
                onKeepScreenOnChange = viewModel::changeKeepScreenOn,
                isUsingFlipPage = viewModel.uiState.isUsingFlipPage,
                onIsUsingFlipPageChange = viewModel::changeIsUsingFlipPage,
                isUsingClickFlip = viewModel.uiState.isUsingClickFlipPage,
                onIsUsingClickFlipChange = viewModel::changeIsUsingClickFlipPage,
                isUsingVolumeKeyFlip = viewModel.uiState.isUsingVolumeKeyFlip,
                onIsUsingVolumeKeyFlipChange = viewModel::changeIsUsingVolumeKeyFlip,
                isUsingFlipAnime = viewModel.uiState.isUsingFlipAnime,
                onIsUsingFlipAnimeChange = viewModel::changeIsUsingFlipAnime,
                enableBatteryIndicator = viewModel.uiState.enableBatteryIndicator,
                onEnableBatteryIndicatorChange = viewModel::changeEnableBatteryIndicator,
                enableTimeIndicator = viewModel.uiState.enableTimeIndicator,
                onEnableTimeIndicatorChange = viewModel::changeEnableTimeIndicator,
                enableReadingChapterProgressIndicator = viewModel.uiState.enableReadingChapterProgressIndicator,
                onEnableReadingChapterProgressIndicatorChange = viewModel::changeEnableReadingChapterProgressIndicator,
                autoPadding = viewModel.uiState.autoPadding,
                onAutoPaddingChange = viewModel::changeAutoPadding,
                topPadding = viewModel.uiState.topPadding,
                onTopPaddingChange = viewModel::changeTopPadding,
                onTopPaddingChangeFinished = viewModel::saveTopPadding,
                bottomPadding = viewModel.uiState.bottomPadding,
                onBottomPaddingChange = viewModel::changeBottomPadding,
                onBottomPaddingChangeFinished = viewModel::saveBottomPadding,
                leftPadding = viewModel.uiState.leftPadding,
                onLeftPaddingChange = viewModel::changeLeftPadding,
                onLeftPaddingChangeFinished = viewModel::saveLeftPadding,
                rightPadding = viewModel.uiState.rightPadding,
                onRightPaddingChange = viewModel::changeRightPadding,
                onRightPaddingChangeFinished = viewModel::saveRightPadding,
            )
        }
        AnimatedVisibility(visible = showChapterSelectorBottomSheet) {
            ChapterSelectorBottomSheet(
                bookVolumes = viewModel.uiState.bookVolumes,
                readingChapterId = viewModel.uiState.chapterContent.id,
                state = chapterSelectorBottomSheetState,
                onDismissRequest = {
                    coroutineScope.launch { chapterSelectorBottomSheetState.hide() }.invokeOnCompletion {
                        if (!chapterSelectorBottomSheetState.isVisible) {
                            showChapterSelectorBottomSheet = false
                        }
                    }
                    showChapterSelectorBottomSheet = false
                },
                onClickChapter = { viewModel.changeChapter(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onClickBackButton: () -> Unit,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onClickBackButton) {
                Icon(painterResource(id = R.drawable.arrow_back_24px), "back")
            }
        },
        title = {
            LazyRow {
                item {
                    AnimatedContent(title, label = "TitleAnimate") {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W400,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    //TODO 全屏
                }) {
                Icon(
                    painter = painterResource(R.drawable.fullscreen_24px),
                    contentDescription = "fullscreen")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun BottomBar(
    chapterContent: ChapterContent,
    readingChapterProgress: Float,
    onClickLastChapter: () -> Unit,
    onClickNextChapter: () -> Unit,
    onClickSettings: () -> Unit,
    onClickChapterSelector: () -> Unit
) {
    BottomAppBar {
        Box(
            Modifier
                .fillMaxHeight()
                .width(12.dp))
        IconButton(
            onClick = onClickLastChapter,
            enabled = chapterContent.hasLastChapter()
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_back_24px),
                contentDescription = "lastChapter")
        }
        IconButton(
            onClick = {
                //TODO 添加至书签
            }) {
            Icon(
                painter = painterResource(R.drawable.outline_bookmark_24px),
                contentDescription = "mark")
        }
        IconButton(onClick = onClickChapterSelector) {
            Icon(painterResource(id = R.drawable.menu_24px), "menu")
        }
        IconButton(onClick = onClickSettings) {
            Icon(
                painter = painterResource(R.drawable.outline_settings_24px),
                contentDescription = "setting")
        }
        Box(
            Modifier
                .padding(9.dp, 12.dp)
                .weight(2f)) {
            Box(Modifier.clip(ButtonDefaults.shape)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(24.dp, 11.5.dp)
                ) {
                    AnimatedText(
                        modifier = Modifier.align(Alignment.Center),
                        text = "${(readingChapterProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.W500
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        IconButton(
            onClick = onClickNextChapter,
            enabled = chapterContent.hasNextChapter()
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_forward_24px),
                contentDescription = "nextChapter")
        }
        Box(
            Modifier
                .fillMaxHeight()
                .width(12.dp))
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    state: SheetState,
    onDismissRequest: () -> Unit,
    fontSize: Float,
    onFontSizeSliderChange: (Float) -> Unit,
    onFontSizeSliderChangeFinished: () -> Unit,
    fontLineHeight: Float,
    onFontLineHeightSliderChange: (Float) -> Unit,
    onFontLineHeightSliderChangeFinished: () -> Unit,
    isKeepScreenOn: Boolean,
    onKeepScreenOnChange: (Boolean) -> Unit,
    isUsingFlipPage: Boolean,
    onIsUsingFlipPageChange: (Boolean) -> Unit,
    isUsingClickFlip: Boolean,
    onIsUsingClickFlipChange: (Boolean) -> Unit,
    isUsingVolumeKeyFlip: Boolean,
    onIsUsingVolumeKeyFlipChange: (Boolean) -> Unit,
    isUsingFlipAnime: Boolean,
    onIsUsingFlipAnimeChange: (Boolean) -> Unit,
    autoPadding: Boolean,
    onAutoPaddingChange: (Boolean) -> Unit,
    enableBatteryIndicator: Boolean,
    onEnableBatteryIndicatorChange: (Boolean) -> Unit,
    enableTimeIndicator: Boolean,
    onEnableTimeIndicatorChange: (Boolean) -> Unit,
    enableReadingChapterProgressIndicator: Boolean,
    onEnableReadingChapterProgressIndicatorChange: (Boolean) -> Unit,
    topPadding: Float,
    onTopPaddingChange: (Float) -> Unit,
    onTopPaddingChangeFinished: () -> Unit,
    bottomPadding: Float,
    onBottomPaddingChange: (Float) -> Unit,
    onBottomPaddingChangeFinished: () -> Unit,
    leftPadding: Float,
    onLeftPaddingChange: (Float) -> Unit,
    onLeftPaddingChangeFinished: () -> Unit,
    rightPadding: Float,
    onRightPaddingChange: (Float) -> Unit,
    onRightPaddingChangeFinished: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state
    ) {
        Box(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 22.dp)
        ) {
            LazyColumn (
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingsSliderEntry(
                        description = "阅读器字体大小",
                        unit = "sp",
                        value = fontSize,
                        valueRange = 8f..64f,
                        onSlideChange = onFontSizeSliderChange,
                        onSliderChangeFinished = onFontSizeSliderChangeFinished
                    )
                }
                item {
                    SettingsSliderEntry(
                        description = "阅读器行距大小",
                        unit = "sp",
                        valueRange = 0f..32f,
                        value = fontLineHeight,
                        onSlideChange = onFontLineHeightSliderChange,
                        onSliderChangeFinished = onFontLineHeightSliderChangeFinished
                    )
                }
                item {
                    SettingsSwitchEntry(
                        title = "屏幕常亮",
                        description = "在阅读页时，总是保持屏幕开启。这将导致耗电量增加",
                        checked = isKeepScreenOn,
                        onCheckedChange = onKeepScreenOnChange,
                    )
                }
                item {
                    SettingsSwitchEntry(
                        title = "翻页模式",
                        description = "切换滚动模式为翻页模式",
                        checked = isUsingFlipPage,
                        onCheckedChange = onIsUsingFlipPageChange,
                    )
                }
                if(isUsingFlipPage) {
                    item {
                        SettingsSwitchEntry(
                            modifier = Modifier.animateItem(),
                            title = "音量键控制",
                            description = "使用音量+键切换至上一页，使用音量-键切换至下一页。",
                            checked = isUsingVolumeKeyFlip,
                            onCheckedChange = onIsUsingVolumeKeyFlipChange,
                        )
                    }
                }
                if(isUsingFlipPage) {
                    item {
                        SettingsSwitchEntry(
                            modifier = Modifier.animateItem(),
                            title = "点击翻页",
                            description = "使用点击控制翻页，并将呼出菜单变为上下滑动。",
                            checked = isUsingClickFlip,
                            onCheckedChange = onIsUsingClickFlipChange,
                        )
                    }
                }
                if(isUsingFlipPage) {
                    item {
                        SettingsSwitchEntry(
                            modifier = Modifier.animateItem(),
                            title = "启用动画",
                            description = "开启点击翻页或音量键翻页时的动画，如果关闭可以允许你快速的翻页。",
                            checked = isUsingFlipAnime,
                            onCheckedChange = onIsUsingFlipAnimeChange,
                        )
                    }
                }
                item {
                    SettingsSwitchEntry(
                        title = "自动获取边距",
                        description = "自动识别手机屏幕的边距，并进行显示适配，如关闭需要手动进行设置。",
                        checked = autoPadding,
                        onCheckedChange = onAutoPaddingChange,
                    )
                }
                item {
                    SettingsSwitchEntry(
                        title = "电量指示器",
                        description = "在页面左下角显示当前电量。",
                        checked = enableBatteryIndicator,
                        onCheckedChange = onEnableBatteryIndicatorChange,
                    )
                }
                item {
                    SettingsSwitchEntry(
                        title = "时间指示器",
                        description = "在页面左下角显示当前时间。",
                        checked = enableTimeIndicator,
                        onCheckedChange = onEnableTimeIndicatorChange,
                    )
                }
                item {
                    SettingsSwitchEntry(
                        title = "进度指示器",
                        description = "在页面右下角显示当前阅读进度。",
                        checked = enableReadingChapterProgressIndicator,
                        onCheckedChange = onEnableReadingChapterProgressIndicatorChange,
                    )
                }
                if(!autoPadding) {
                    item {
                        SettingsSliderEntry(
                            modifier = Modifier.animateItem(),
                            description = "上边距",
                            unit = "dp",
                            value = topPadding,
                            valueRange = 0f..128f,
                            onSlideChange = onTopPaddingChange,
                            onSliderChangeFinished = onTopPaddingChangeFinished
                        )
                    }
                }
                if(!autoPadding) {
                    item {
                        SettingsSliderEntry(
                            modifier = Modifier.animateItem(),
                            description = "下边距",
                            unit = "dp",
                            value = bottomPadding,
                            valueRange = 0f..128f,
                            onSlideChange = onBottomPaddingChange,
                            onSliderChangeFinished = onBottomPaddingChangeFinished
                        )
                    }
                }
                if(!autoPadding) {
                    item {
                        SettingsSliderEntry(
                            modifier = Modifier.animateItem(),
                            description = "左边距",
                            unit = "dp",
                            value = leftPadding,
                            valueRange = 0f..128f,
                            onSlideChange = onLeftPaddingChange,
                            onSliderChangeFinished = onLeftPaddingChangeFinished
                        )
                    }
                }
                if(!autoPadding) {
                    item {
                        SettingsSliderEntry(
                            modifier = Modifier.animateItem(),
                            description = "右边距",
                            unit = "dp",
                            value = rightPadding,
                            valueRange = 0f..128f,
                            onSlideChange = onRightPaddingChange,
                            onSliderChangeFinished = onRightPaddingChangeFinished
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSelectorBottomSheet(
    bookVolumes: BookVolumes,
    readingChapterId: Int,
    state: SheetState,
    onDismissRequest: () -> Unit,
    onClickChapter: (Int) -> Unit
) {
    var selectId by remember { mutableStateOf(0) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = state
    ) {
        LazyColumn (
            modifier = Modifier.padding(18.dp, 0.dp, 18.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.read_more_24px),
                        contentDescription = null
                    )
                    Text(
                        text = "章节选择",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.W700,
                        fontSize = 18.sp,
                        lineHeight = 32.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            items(bookVolumes.volumes) { volume ->
                FilledCard(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        if (selectId == volume.volumeId) {
                            selectId = -1
                            return@FilledCard
                        }
                        selectId = volume.volumeId
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.5.dp, 5.dp, 10.dp, 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = volume.volumeTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = volume.chapters.size.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W500
                            )
                        }
                        Box(Modifier.weight(2f))
                        Icon(
                            modifier = Modifier
                                .scale(0.75f, 0.75f)
                                .rotate(if (selectId == volume.volumeId) -90f else 90f),
                            painter = painterResource(R.drawable.arrow_forward_ios_24px),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = null
                        )
                    }
                }
                AnimatedVisibility(
                    visible = selectId == volume.volumeId
                ) {
                    Column {
                        volume.chapters.forEach { chapterInformation ->
                            AnimatedVisibility(
                                visible = readingChapterId != chapterInformation.id
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(7.5.dp, 2.dp)
                                        .clickable {
                                            onClickChapter(chapterInformation.id)
                                        },
                                    text = chapterInformation.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.W400,
                                    lineHeight = 28.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AnimatedVisibility(
                                visible = readingChapterId == chapterInformation.id
                            ) {
                                FilledCard(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.75f
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(7.5.dp, 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = chapterInformation.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.W400,
                                            lineHeight = 28.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Box(Modifier.weight(2f))
                                        Icon(
                                            painter = painterResource(R.drawable.check_24px),
                                            tint = MaterialTheme.colorScheme.outline,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    enableBatteryIndicator: Boolean,
    enableTimeIndicator: Boolean,
    enableReadingChapterProgressIndicator: Boolean,
    readingChapterProgress: Float
) {
    val batteryManager = LocalContext.current.getSystemService(BATTERY_SERVICE) as BatteryManager
    val batLevel: Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    Row(
        modifier = modifier.fillMaxWidth().height(46.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (enableBatteryIndicator)
            Icon(
                modifier = Modifier.size(20.dp),
                painter =
                    when {
                        (batLevel == 0) -> painterResource(R.drawable.battery_horiz_000_24px)
                        (batLevel in 1..10) -> painterResource(R.drawable.battery_very_low_24px)
                        (batLevel in 11..35) -> painterResource(R.drawable.battery_low_24px)
                        (batLevel in 36..65) -> painterResource(R.drawable.battery_horiz_050_24px)
                        (batLevel in 66..90) -> painterResource(R.drawable.battery_horiz_075_24px)
                        (batLevel in 91..100) -> painterResource(R.drawable.battery_full_alt_24px)
                        else -> painterResource(R.drawable.battery_horiz_000_24px)
                    },
                contentDescription = null
            )
        if (enableTimeIndicator)
            AnimatedText(
                text = "${LocalDateTime.now().hour} : " + LocalDateTime.now().minute.let { if (it < 10) "0$it" else it.toString()},
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.W500
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        Box(Modifier.weight(1f))
        if (enableReadingChapterProgressIndicator)
            AnimatedText(
                text = "${(readingChapterProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.W500
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
    }
}