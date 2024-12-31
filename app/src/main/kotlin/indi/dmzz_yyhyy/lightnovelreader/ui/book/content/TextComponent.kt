package indi.dmzz_yyhyy.lightnovelreader.ui.book.content

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.compose.AsyncImage
import coil.request.ImageRequest
import indi.dmzz_yyhyy.lightnovelreader.AppEvent
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun ContentText(
    content: String,
    onClickLastChapter: () -> Unit,
    onClickNextChapter: () -> Unit,
    fontSize: TextUnit,
    fontLineHeight: TextUnit,
    readingProgress: Float,
    isUsingFlipPage: Boolean,
    isUsingClickFlip: Boolean,
    isUsingVolumeKeyFlip: Boolean,
    flipAnime: String,
    fastChapterChange: Boolean,
    onChapterReadingProgressChange: (Float) -> Unit,
    changeIsImmersive: () -> Unit,
    paddingValues: PaddingValues,
    autoPadding: Boolean
) {
    val autoAvoidPaddingValues = with(LocalDensity.current) {
        PaddingValues(
            top = paddingValues.calculateTopPadding() +
                    WindowInsets.displayCutout.getTop(LocalDensity.current).toDp(),
            bottom = paddingValues.calculateBottomPadding() +
                    WindowInsets.displayCutout.getBottom(LocalDensity.current).toDp(),
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr) +
                    WindowInsets.displayCutout.getLeft(LocalDensity.current, LayoutDirection.Ltr).toDp(),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr) +
                    WindowInsets.displayCutout.getRight(LocalDensity.current, LayoutDirection.Ltr).toDp(),
        )
    }
    if (!isUsingFlipPage)
        ScrollContentTextComponent(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
                .padding(paddingValues)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = changeIsImmersive
                ),
            content = content,
            fontSize = fontSize,
            fontLineHeight = fontLineHeight,
            readingProgress = readingProgress,
            onChapterReadingProgressChange = onChapterReadingProgressChange
        )
    else
        SimpleFlipPageTextComponent(
            modifier = Modifier.fillMaxSize(),
            content = content,
            onClickLastChapter = onClickLastChapter,
            onClickNextChapter = onClickNextChapter,
            fontSize = fontSize,
            fontLineHeight = fontLineHeight,
            readingProgress = readingProgress,
            isUsingClickFlip = isUsingClickFlip,
            isUsingVolumeKeyFlip = isUsingVolumeKeyFlip,
            flipAnime = flipAnime,
            fastChapterChange = fastChapterChange,
            onChapterReadingProgressChange = onChapterReadingProgressChange,
            changeIsImmersive = changeIsImmersive,
            paddingValues =
                if (autoPadding) autoAvoidPaddingValues else paddingValues
        )
}

@Composable
fun ScrollContentTextComponent(
    modifier: Modifier,
    content: String,
    fontSize: TextUnit,
    fontLineHeight: TextUnit,
    readingProgress: Float,
    onChapterReadingProgressChange: (Float) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val contentLazyColumnState = rememberLazyListState()
    var contentKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(readingProgress) {
        if (contentKey == content.hashCode()) return@LaunchedEffect
        contentKey = content.hashCode()
        coroutineScope.launch {
            contentLazyColumnState.scrollToItem(
                0,
                ((contentLazyColumnState.layoutInfo.visibleItemsInfo.sumOf { it.size } - contentLazyColumnState.layoutInfo.viewportSize.height) *
                        readingProgress).toInt()
            )
        }
    }
    LaunchedEffect(contentLazyColumnState.firstVisibleItemScrollOffset) {
        val visibleItemsHeight = contentLazyColumnState.layoutInfo.visibleItemsInfo.sumOf { it.size }
        val viewportHeight = contentLazyColumnState.layoutInfo.viewportSize.height
        val progress = contentLazyColumnState.firstVisibleItemScrollOffset.toFloat() /
                (visibleItemsHeight - viewportHeight)
        onChapterReadingProgressChange(progress)
    }
    LazyColumn(
        modifier = modifier,
        state = contentLazyColumnState,
    ) {
        items(
            content
                .split("[image]")
                .filter { it.isNotBlank() }
        ) {
            BasicContentComponent(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                fontSize = fontSize,
                fontLineHeight = fontLineHeight,
            )
        }
    }
}

@Composable
fun SimpleFlipPageTextComponent(
    modifier: Modifier,
    content: String,
    onClickLastChapter: () -> Unit,
    onClickNextChapter: () -> Unit,
    fontSize: TextUnit,
    fontLineHeight: TextUnit,
    readingProgress: Float,
    isUsingClickFlip: Boolean,
    isUsingVolumeKeyFlip: Boolean,
    flipAnime: String,
    fastChapterChange: Boolean,
    onChapterReadingProgressChange: (Float) -> Unit,
    changeIsImmersive: () -> Unit,
    paddingValues: PaddingValues
) {
    val textMeasurer = rememberTextMeasurer()
    val scope = rememberCoroutineScope()
    val current = LocalContext.current
    var contentKey by remember { mutableIntStateOf(0) }
    var slipTextJob by remember { mutableStateOf<Job?>(null) }
    var resumedReadingProgressJob by remember { mutableStateOf<Job?>(null) }
    var constraints by remember { mutableStateOf<Constraints?>(null) }
    var textStyle by remember { mutableStateOf<TextStyle?>(null) }
    var slippedTextList by remember { mutableStateOf(emptyList<String>()) }
    var pagerState by remember { mutableStateOf(PagerState { 0 }) }
    var readingPageFistCharOffset by remember { mutableIntStateOf(0) }
    var resumedReadingProgress by remember { mutableStateOf(false) }
    fun lastPage() {
        if (pagerState.currentPage != 0)
            scope.launch {
                if (flipAnime != MenuOptions.FlipAnimeOptions.None)
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                else
                    pagerState.scrollToPage(pagerState.currentPage - 1)
            }
        else if (fastChapterChange) onClickLastChapter.invoke()
    }

    fun nextPage() {
        if (pagerState.currentPage + 1 < pagerState.pageCount)
            scope.launch {
                if (flipAnime != MenuOptions.FlipAnimeOptions.None)
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                else
                    pagerState.scrollToPage(pagerState.currentPage + 1)
            }
        else if (fastChapterChange) onClickNextChapter.invoke()
    }

    LaunchedEffect(content, textStyle, fontLineHeight, fontSize, constraints?.maxHeight, constraints?.maxWidth) {
        val key = content.hashCode() + fontLineHeight.value.hashCode() + fontSize.value.hashCode() + constraints?.maxHeight.hashCode() + constraints?.maxWidth.hashCode()
        if (constraints == null || textStyle == null || key == contentKey) return@LaunchedEffect
        contentKey = key
        slipTextJob?.cancel()
        slipTextJob = scope.launch(Dispatchers.IO) {
            readingPageFistCharOffset = slippedTextList
                .subList(0, pagerState.currentPage)
                .sumOf { it.length }
                .plus(1)
            slippedTextList = slipText(
                textMeasurer = textMeasurer,
                constraints = constraints!!,
                text = content,
                style = textStyle!!.copy(
                    fontSize = fontSize,
                    fontWeight = FontWeight.W400,
                    lineHeight = (fontLineHeight.value + fontSize.value).sp
                )
            )
            pagerState = PagerState { slippedTextList.size }
            resumedReadingProgressJob?.cancel()
            resumedReadingProgressJob = scope.launch {
                pagerState.scrollToPage((readingProgress * pagerState.pageCount).toInt())
                resumedReadingProgress = true
            }
        }
    }
    LaunchedEffect(pagerState.currentPage, pagerState.pageCount) {
        if (pagerState.pageCount != 1)
            onChapterReadingProgressChange(pagerState.currentPage.toFloat() / (pagerState.pageCount - 1))
        else onChapterReadingProgressChange(1F)
    }
    DisposableEffect(isUsingVolumeKeyFlip, flipAnime, fastChapterChange) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(current)
        val keycodeVolumeUpReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isUsingVolumeKeyFlip)
                    lastPage()
            }
        }
        val keycodeVolumeDownReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isUsingVolumeKeyFlip)
                    nextPage()
            }
        }
        localBroadcastManager.registerReceiver(keycodeVolumeUpReceiver, IntentFilter(AppEvent.KEYCODE_VOLUME_UP))
        localBroadcastManager.registerReceiver(keycodeVolumeDownReceiver, IntentFilter(AppEvent.KEYCODE_VOLUME_DOWN))
        onDispose {
            localBroadcastManager.unregisterReceiver(keycodeVolumeUpReceiver)
            localBroadcastManager.unregisterReceiver(keycodeVolumeDownReceiver)
        }
    }
    LocalContext.current.resources.displayMetrics.let { displayMetrics ->
        constraints = Constraints(
            maxWidth = displayMetrics
                .widthPixels
                .minus(
                    with(LocalDensity.current) {
                        (paddingValues.calculateStartPadding(LayoutDirection.Ltr) + paddingValues.calculateEndPadding(LayoutDirection.Ltr))
                            .toPx()
                    }.toInt()
                ),
            maxHeight = displayMetrics
                .heightPixels
                .minus(
                    with(LocalDensity.current) {
                        (paddingValues.calculateTopPadding() + paddingValues.calculateBottomPadding() + 10.dp)
                            .toPx()
                    }.toInt()
                ),
        )
    }
    textStyle = MaterialTheme.typography.bodyMedium
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .draggable(
                enabled = isUsingClickFlip,
                interactionSource = remember { MutableInteractionSource() },
                orientation = Orientation.Vertical,
                state = rememberDraggableState {},
                onDragStopped = {
                    if (it.absoluteValue > 60) changeIsImmersive.invoke()
                }
            )
            .pointerInput(isUsingClickFlip, flipAnime, fastChapterChange) {
                detectTapGestures(
                    onTap = {
                        if (isUsingClickFlip)
                            if (it.x <= current.resources.displayMetrics.widthPixels * 0.425) lastPage()
                            else nextPage()
                        else changeIsImmersive.invoke()
                    }
                )
            },
        ) {
            BasicContentComponent(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                text = slippedTextList[it],
                fontSize = fontSize,
                fontLineHeight = fontLineHeight,
            )
    }
}

@Composable
fun BasicContentComponent(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    fontLineHeight: TextUnit,
) {
    if (text.startsWith("http://") || text.startsWith("https://")) {
        Box(modifier
            .fillMaxWidth()
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(text)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
        }
    } else
        SelectionContainer {
            Text(
                modifier = modifier.fillMaxSize(),
                text = text,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W400,
                fontSize = fontSize,
                lineHeight = (fontSize.value + fontLineHeight.value).sp
            )
        }
}

fun slipText(
    textMeasurer: TextMeasurer,
    constraints: Constraints,
    text: String,
    style: TextStyle,
): List<String> {
    val resultList: MutableList<String> = mutableListOf()
    text.split("[image]").filter { it.isNotEmpty() }.forEach {sigleText ->
        if (sigleText.startsWith("http://") || sigleText.startsWith("https://"))
            resultList.add(sigleText)
        else {
            textMeasurer
                .measure(
                    text = sigleText,
                    style = style,
                    constraints = constraints
                )
                .getSlipString(text, constraints)
                .let(resultList::addAll)
        }
    }
    return resultList
}

fun TextLayoutResult.getSlipString(text: String, constraints: Constraints): List<String> {
    val result: MutableList<String> = mutableListOf()
    var lastLine = 0
    fun getNotOverflowText(startLine: Int): String {
        fun getNotOverflowLine(): Int {
            val startHeight = getLineTop(startLine)
            fun isLineOverflow(line: Int): Boolean = getLineBottom(line) > startHeight + constraints.maxHeight
            var checkLine = getLineForOffset(getOffsetForPosition(Offset(constraints.maxWidth.toFloat(), startHeight + constraints.maxHeight)))
            while (isLineOverflow(checkLine))
                checkLine--
            return checkLine
        }
        val startTextOffset = getLineStart(startLine)
        lastLine = getNotOverflowLine()
        val endTextOffset = getLineEnd(lastLine)
        lastLine++
        return text.slice(startTextOffset..<endTextOffset)
    }
    while(lastLine != this.lineCount) {
        getNotOverflowText(lastLine).let(result::add)
    }
    return result.filter { it.isNotBlank() }
}