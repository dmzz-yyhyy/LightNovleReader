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
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    paddingValues: PaddingValues,
    onClickBackButton: () -> Unit,
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    bookId: Int,
    chapterId: Int,
    viewModel: ContentViewModel = hiltViewModel()
) {
    Box(Modifier.padding(
        start = paddingValues.calculateStartPadding(LayoutDirection.Rtl),
        top = paddingValues.calculateTopPadding(),
        bottom = 0.dp,
        end = paddingValues.calculateEndPadding(LayoutDirection.Rtl),
    )) {
        Content(
            onClickBackButton = onClickBackButton,
            topBar = topBar,
            bookId = bookId,
            chapterId = chapterId,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    onClickBackButton: () -> Unit,
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    bookId: Int,
    chapterId: Int,
    viewModel: ContentViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current
    val settingsBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val chapterSelectorBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isRunning by remember { mutableStateOf(false) }
    var isImmersive by remember { mutableStateOf(false) }
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    var showChapterSelectorBottomSheet by remember { mutableStateOf(false) }
    var totalReadingTime by remember { mutableIntStateOf(0) }
    var selectedVolumeId by remember { mutableIntStateOf(-1) }

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
    LaunchedEffect(viewModel.uiState.bookVolumes) {
        selectedVolumeId = viewModel.uiState.bookVolumes.volumes.firstOrNull { volume -> volume.chapters.any { it.id == chapterId } }?.volumeId ?: -1
    }
    LifecycleResumeEffect(Unit) {
        isRunning = true
        onPauseOrDispose {
            isRunning = false
            viewModel.updateTotalReadingTime(bookId, totalReadingTime)
            totalReadingTime = 0
        }
    }
    LaunchedEffect(viewModel.settingState.keepScreenOn) {
        if (viewModel.settingState.keepScreenOn)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    LaunchedEffect(isRunning) {
        while (isRunning) {
            totalReadingTime += 1
            if (totalReadingTime > 300) {
                viewModel.updateTotalReadingTime(bookId, totalReadingTime)
                totalReadingTime = 0
            }
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
            val isEnableIndicator = viewModel.settingState.enableBatteryIndicator || viewModel.settingState.enableTimeIndicator || viewModel.settingState.enableReadingChapterProgressIndicator
            Box(Modifier.fillMaxSize()) {
                AnimatedContent(
                    viewModel.uiState.chapterContent.content,
                    label = "ContentAnimate"
                ) { text ->
                    ContentText(
                        content = text,
                        onClickLastChapter = viewModel::lastChapter,
                        onClickNextChapter = viewModel::nextChapter,
                        fontSize = viewModel.settingState.fontSize.sp,
                        fontLineHeight = viewModel.settingState.fontLineHeight.sp,
                        readingProgress = viewModel.uiState.readingProgress,
                        isUsingFlipPage = viewModel.settingState.isUsingFlipPage,
                        isUsingClickFlip = viewModel.settingState.isUsingClickFlipPage,
                        isUsingVolumeKeyFlip = viewModel.settingState.isUsingVolumeKeyFlip,
                        flipAnime = viewModel.settingState.flipAnime,
                        onChapterReadingProgressChange = viewModel::changeChapterReadingProgress,
                        changeIsImmersive = { isImmersive = !isImmersive },
                        paddingValues =
                        if (viewModel.settingState.autoPadding)
                            PaddingValues(
                                top = 12.dp,
                                bottom = if (isEnableIndicator) 46.dp else 12.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        else PaddingValues(
                            top = viewModel.settingState.topPadding.dp,
                            bottom = if (isEnableIndicator) (viewModel.settingState.bottomPadding + 38).dp else viewModel.settingState.bottomPadding.dp,
                            start = viewModel.settingState.leftPadding.dp,
                            end = viewModel.settingState.rightPadding.dp
                        ),
                        autoPadding = viewModel.settingState.autoPadding,
                        fastChapterChange = viewModel.settingState.fastChapterChange
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
                                if (viewModel.settingState.autoPadding)
                                    PaddingValues(
                                        bottom = 8.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    )
                                else PaddingValues(
                                    bottom = viewModel.settingState.bottomPadding.dp,
                                    start = viewModel.settingState.leftPadding.dp,
                                    end = viewModel.settingState.rightPadding.dp
                                )
                            ),
                        enableBatteryIndicator = viewModel.settingState.enableBatteryIndicator,
                        enableTimeIndicator = viewModel.settingState.enableTimeIndicator,
                        enableChapterTitle = viewModel.settingState.enableChapterTitleIndicator,
                        chapterTitle = viewModel.uiState.chapterContent.title,
                        enableReadingChapterProgressIndicator = viewModel.settingState.enableReadingChapterProgressIndicator,
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
                sheetState = settingsBottomSheetState,
                onDismissRequest = {
                    coroutineScope.launch { settingsBottomSheetState.hide() }.invokeOnCompletion {
                        if (!settingsBottomSheetState.isVisible) {
                            showSettingsBottomSheet = false
                        }
                    }
                    showSettingsBottomSheet = false
                },
                settingState = viewModel.settingState,
                uiState = viewModel.uiState
            )
        }
        ChapterSelectorBottomSheet(
            display = showChapterSelectorBottomSheet,
            selectedVolumeId = selectedVolumeId,
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
                selectedVolumeId = viewModel.uiState.bookVolumes.volumes.firstOrNull { volume -> volume.chapters.any { it.id == chapterId } }?.volumeId ?: -1
            },
            onClickChapter = { viewModel.changeChapter(it) },
            onChangeSelectedVolumeId = {
                selectedVolumeId = it
            }
        )
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
fun ChapterSelectorBottomSheet(
    display: Boolean,
    selectedVolumeId: Int,
    bookVolumes: BookVolumes,
    readingChapterId: Int,
    state: SheetState,
    onDismissRequest: () -> Unit,
    onClickChapter: (Int) -> Unit,
    onChangeSelectedVolumeId: (Int) -> Unit
) {
    val lazyColumnState = rememberLazyListState()
    LaunchedEffect(selectedVolumeId) {
        if (selectedVolumeId != -1)
            lazyColumnState.animateScrollToItem(selectedVolumeId)
    }
    AnimatedVisibility(visible = display) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = state
        ) {
            LazyColumn(
                modifier = Modifier.padding(18.dp, 0.dp, 18.dp, 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyColumnState
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
                            if (selectedVolumeId == volume.volumeId) {
                                onChangeSelectedVolumeId(-1)
                                return@FilledCard
                            }
                            onChangeSelectedVolumeId(volume.volumeId)
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
                                    .rotate(if (selectedVolumeId == volume.volumeId) -90f else 90f),
                                painter = painterResource(R.drawable.arrow_forward_ios_24px),
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = selectedVolumeId == volume.volumeId
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
}

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    enableBatteryIndicator: Boolean,
    enableTimeIndicator: Boolean,
    enableChapterTitle: Boolean,
    chapterTitle: String,
    enableReadingChapterProgressIndicator: Boolean,
    readingChapterProgress: Float
) {
    val current = LocalDensity.current
    val batteryManager = LocalContext.current.getSystemService(BATTERY_SERVICE) as BatteryManager
    val batLevel: Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    var progressIndicatorWidth by remember { mutableStateOf(0.dp) }
    Row(
        modifier = modifier.fillMaxWidth().height(46.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
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
                    text = "${LocalDateTime.now().hour} : " + LocalDateTime.now().minute.let { if (it < 10) "0$it" else it.toString() },
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
        Box(Modifier.width(12.dp))
        Box {
            if (enableChapterTitle)
                LazyRow(Modifier.align(Alignment.CenterStart).padding(end = progressIndicatorWidth + 12.dp)) {
                    item {
                        AnimatedText(
                            text = chapterTitle,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.W500
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            if (enableReadingChapterProgressIndicator) {
                AnimatedText(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .onGloballyPositioned { layoutCoordinates ->
                            with(current) {
                                progressIndicatorWidth = layoutCoordinates.size.width.toDp()
                            }
                        },
                    text = "${(readingChapterProgress * 100).toInt()}%",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}