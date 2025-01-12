package indi.dmzz_yyhyy.lightnovelreader.ui.book.detail

import android.content.Intent
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkInfo
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.Volume
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Loading
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.launcher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onClickBackButton: () -> Unit,
    onClickChapter: (Int) -> Unit,
    onClickReadFromStart: () -> Unit = {
        viewModel.uiState.bookVolumes.volumes.firstOrNull()?.chapters?.firstOrNull()?.id?.let {
            onClickChapter(it)
        }
    },
    onClickContinueReading: () -> Unit = {
        if (viewModel.uiState.userReadingData.lastReadChapterId == -1)
            viewModel.uiState.bookVolumes.volumes.firstOrNull()?.chapters?.firstOrNull()?.id?.let {
                onClickChapter(it)
            }
        else
            onClickChapter(viewModel.uiState.userReadingData.lastReadChapterId)
    },
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    id: Int,
    cacheBook: (Int) -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit
) {
    Box(Modifier.padding(paddingValues)) {
        Content(
            viewModel = viewModel,
            onClickBackButton = onClickBackButton,
            onClickChapter = onClickChapter,
            onClickReadFromStart = onClickReadFromStart,
            onClickContinueReading = onClickContinueReading,
            topBar = topBar,
            id = id,
            cacheBook = cacheBook,
            requestAddBookToBookshelf = requestAddBookToBookshelf
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    viewModel: DetailViewModel = hiltViewModel(),
    onClickBackButton: () -> Unit,
    onClickChapter: (Int) -> Unit,
    onClickReadFromStart: () -> Unit = {
        viewModel.uiState.bookVolumes.volumes.firstOrNull()?.chapters?.firstOrNull()?.id?.let {
            onClickChapter(it)
        }
    },
    onClickContinueReading: () -> Unit = {
        if (viewModel.uiState.userReadingData.lastReadChapterId == -1)
            viewModel.uiState.bookVolumes.volumes.firstOrNull()?.chapters?.firstOrNull()?.id?.let {
                onClickChapter(it)
            }
        else
            onClickChapter(viewModel.uiState.userReadingData.lastReadChapterId)
    },
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    id: Int,
    cacheBook: (Int) -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState = viewModel.uiState
    @Suppress("SENSELESS_COMPARISON")
    val exportToEPUBLauncher = launcher {
        scope.launch {
            Toast.makeText(context, "开始导出书本 ${viewModel.uiState.bookInformation.title}", Toast.LENGTH_SHORT).show()
            viewModel.exportToEpub(it, id).collect {
                if (it != null)
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Toast.makeText(context, "成功导出书本 ${viewModel.uiState.bookInformation.title}", Toast.LENGTH_SHORT).show()
                        }
                        WorkInfo.State.FAILED -> {
                            Toast.makeText(context, "导出书本 ${viewModel.uiState.bookInformation.title} 失败", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
            }
        }
    }
    topBar {
        TopBar(
            onClickBackButton = onClickBackButton,
            onClickExport = { createDataFile(viewModel.uiState.bookInformation.title, exportToEPUBLauncher) },
            scrollBehavior = it
        )
    }
    LaunchedEffect(id) {
        viewModel.init(id)
    }
    AnimatedVisibility(
        visible = viewModel.uiState.bookInformation.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Loading()
    }
    AnimatedVisibility(
        visible =  !viewModel.uiState.bookInformation.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                BookCard(uiState.bookInformation)
            }
            item {
                QuickOperationsRow(
                    onClickAddToBookShelf = { requestAddBookToBookshelf(uiState.bookInformation.id) },
                    onClickCache = { cacheBook(uiState.bookInformation.id) },
                    onClickTags = {}
                )
            }
            item {
                Description(uiState.bookInformation.description)
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.detail_contents),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        painter = painterResource(R.drawable.filter_list_24px),
                        contentDescription = null
                        /* TODO: 排序方式 */
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible =  viewModel.uiState.bookVolumes.volumes.isEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Loading()
                }
            }
            items(uiState.bookVolumes.volumes) {
                VolumeItem(
                    volume = it,
                    readCompletedChapterIds = uiState.userReadingData.readCompletedChapterIds,
                    onClickChapter = onClickChapter
                )
            }
        }
        Box(Modifier.fillMaxSize().padding(end = 31.dp, bottom = 54.dp)) {
            ExtendedFloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick =
                if (uiState.userReadingData.lastReadChapterId == -1) onClickReadFromStart else onClickContinueReading,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.filled_menu_book_24px),
                        contentDescription = null
                    )
                },
                text = { Text(text = if (uiState.userReadingData.lastReadChapterId == -1) "开始阅读" else stringResource(id = R.string.continue_reading)) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onClickBackButton: () -> Unit,
    onClickExport: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            LazyRow {
                item {
                    Text(
                        text = "详情",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W400,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onClickExport
            ) {
                Icon(painterResource(id = R.drawable.file_export_24px), "export to epub")
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onClickBackButton) {
                Icon(painterResource(id = R.drawable.arrow_back_24px), "back")
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun BookCard(bookInformation: BookInformation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp, 6.dp, 4.dp, 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cover(
            width = 110.dp,
            height = 164.dp,
            url = bookInformation.coverUrl,
            rounded = 14.dp
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = bookInformation.title,
                maxLines = 2,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 22.sp,
                fontWeight = FontWeight.W600,
                lineHeight = 28.0.sp
            )
            Text(
                text = bookInformation.author,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(R.drawable.text_snippet_24px),
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${bookInformation.wordCount/1000}K 字",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "·",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = bookInformation.publishingHouse,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter =
                    if (bookInformation.isComplete)
                        painterResource(R.drawable.done_all_24px)
                    else
                        painterResource(R.drawable.autorenew_24px),
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text =
                    if (bookInformation.isComplete) "已完结" else "连载中",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "·",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = bookInformation.lastUpdated.let {
                        StringBuilder()
                            .append("最后更新: ")
                            .append(it.year)
                            .append("-")
                            .append(it.monthValue)
                            .append("-")
                            .append(it.dayOfMonth)
                            .toString()
                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuickOperationsRow(
    onClickAddToBookShelf: () -> Unit,
    onClickCache: () -> Unit,
    onClickTags: () -> Unit
) {
    @Composable
    fun QuickOperationButton(
        icon: Painter,
        title: String,
        onClick: () -> Unit,
    ) {
        Button(
            modifier = Modifier.height(72.dp),
            colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = RoundedCornerShape(16.dp),
            onClick = onClick
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickOperationButton(
            icon = painterResource(R.drawable.bookmark_add_24px),
            title = "添加至书架",
            onClick = onClickAddToBookShelf
        )
        VerticalDivider(
            modifier = Modifier.height(22.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        QuickOperationButton(
            icon = painterResource(R.drawable.cloud_download_24px),
            title = "缓存至本地",
            onClick = onClickCache
        )
        VerticalDivider(
            modifier = Modifier.height(22.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        QuickOperationButton(
            icon = painterResource(R.drawable.tag_24px),
            title = "标签",
            onClick = onClickTags
        )
    }
}

@Composable
private fun Description(description: String) {
    var isNeedExpand by remember { mutableStateOf(false) }
    var expandSummaryText by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.detail_introduction),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.W600,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            modifier = Modifier.animateContentSize(),
            text = description,
            maxLines = if (expandSummaryText) Int.MAX_VALUE else 3,
            onTextLayout = {
                isNeedExpand = it.hasVisualOverflow || isNeedExpand
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
        AnimatedVisibility(isNeedExpand) {
            Box(Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    onClick = { expandSummaryText = !expandSummaryText }
                ) {
                    Icon(
                        modifier = Modifier.rotate(if (expandSummaryText) 0f else 180f),
                        painter = painterResource(R.drawable.keyboard_arrow_up_24px),
                        contentDescription = "expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (expandSummaryText) "收起" else "展开",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun VolumeItem(
    volume: Volume,
    readCompletedChapterIds: List<Int>,
    onClickChapter: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.height(48.dp).padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = volume.volumeTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp
            )
            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = volume.chapters.size.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
            }
        }
        volume.chapters.forEach {
            Box(
                modifier = Modifier
                    .clickable { onClickChapter(it.id) }
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = it.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        fontWeight =
                        if (readCompletedChapterIds.contains(it.id))
                            FontWeight.W400
                        else FontWeight.W600,
                        color =
                        if (readCompletedChapterIds.contains(it.id))
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Suppress("DuplicatedCode")
fun createDataFile(fileName: String, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val initUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Documents")
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/epub+zip"
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri)
        putExtra(Intent.EXTRA_TITLE, fileName)
    }
    launcher.launch(Intent.createChooser(intent, "选择一位置"))
}