package indi.dmzz_yyhyy.lightnovelreader.ui.book.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.Volume
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Loading
import indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home.BookStatusIcon

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
    onClickMore: () -> Unit = {
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
            onClickMore = onClickMore,
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
    onClickMore: () -> Unit = {
    },
    topBar: (@Composable (TopAppBarScrollBehavior) -> Unit) -> Unit,
    id: Int,
    cacheBook: (Int) -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit
) {
    val uiState = viewModel.uiState

    topBar {
        TopBar(
            onClickBackButton = onClickBackButton,
            onClickMore = onClickMore,
            scrollBehavior = it
        )
    }
    LaunchedEffect(id) {
        viewModel.init(id)
    }
    AnimatedVisibility(
        visible =  viewModel.uiState.bookInformation.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Loading()
    }
    AnimatedVisibility(
        visible = !viewModel.uiState.bookInformation.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                BookCardBlock(uiState.bookInformation)
            }
            item {
                QuickOperationsBlock(
                    onClickAddToBookShelf = { requestAddBookToBookshelf(uiState.bookInformation.id) },
                    onClickCache = { cacheBook(uiState.bookInformation.id) },
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                IntroBlock(uiState.bookInformation.description)
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                ) {
                    Text(
                        text = stringResource(R.string.detail_contents),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
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
                    onClickChapter = onClickChapter,
                    volumesSize = uiState.bookVolumes.volumes.size
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(end = 31.dp, bottom = 54.dp)
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = if (uiState.userReadingData.lastReadChapterId == -1) onClickReadFromStart
                    else onClickContinueReading,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.filled_menu_book_24px),
                        contentDescription = null
                    )
                },
                text = {
                    Text(if (uiState.userReadingData.lastReadChapterId == -1) "开始阅读"
                    else stringResource(id = R.string.continue_reading))
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onClickBackButton: () -> Unit,
    onClickMore: () -> Unit,
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
        /*actions = {
            IconButton(
                onClick = onClickMore
            ) {
                Icon(painterResource(id = R.drawable.more_vert_24px), "more")
            }
        },*/
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
private fun BookCardBlock(bookInformation: BookInformation) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
    ) {
        Cover(
            height = 178.dp,
            width = 122.dp,
            url = bookInformation.coverUrl,
            rounded = 8.dp
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .height(178.dp)
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val titleLineHeight = 24.sp
            Text(
                modifier = Modifier.height(
                    with(LocalDensity.current) { (titleLineHeight * 3.3f).toDp() }
                ).wrapContentHeight(Alignment.CenterVertically),
                text = bookInformation.title,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                lineHeight = titleLineHeight,
            )
            /*Text(
                text = bookInformation.subtitle
                maxLines = 1,
                color = MaterialTheme.colorScheme.secondary,
                lineHeight = 17.sp,
                fontSize = 15.sp,
            )*/
            Text(
                text = bookInformation.author,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 20.sp,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookStatusIcon(bookInformation)
                    Text(
                        text = if (bookInformation.isComplete) "已完结" else stringResource(
                            R.string.book_info_update_date,
                            bookInformation.lastUpdated.year,
                            bookInformation.lastUpdated.monthValue,
                            bookInformation.lastUpdated.dayOfMonth
                        ),
                        maxLines = 1,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp).padding(top = 2.dp),
                        painter = painterResource(R.drawable.text_snippet_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "${bookInformation.wordCount / 1000}K 字",
                        maxLines = 1,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp,vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SuggestionChip(
                label = {
                    Text(bookInformation.publishingHouse)
                },
                onClick = {}
            )
        }
        items(bookInformation.tags) { tag ->
            SuggestionChip(
                label = {
                    Text(tag)
                },
                onClick = {}
            )
        }
    }
}

@Composable
private fun QuickOperationsBlock(
    onClickAddToBookShelf: () -> Unit,
    onClickCache: () -> Unit,
) {
    @Composable
    fun QuickOperationButton(
        icon: Painter,
        title: String,
        onClick: () -> Unit,
    ) {
        Button(
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .height(72.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = RoundedCornerShape(0.dp),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            QuickOperationButton(
                icon = painterResource(R.drawable.bookmark_add_24px),
                title = "添加至书架",
                onClick = onClickAddToBookShelf
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            QuickOperationButton(
                icon = painterResource(R.drawable.cloud_download_24px),
                title = "缓存至本地",
                onClick = onClickCache
            )
        }
    }
}


@Composable
private fun IntroBlock(description: String) {
    var overflowed by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.detail_introduction),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.animateContentSize(),
            text = description,
            fontSize = 15.sp,
            maxLines = if (!expanded) 3 else 80,
            onTextLayout = {
                overflowed = it.hasVisualOverflow || expanded
            },
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis,
        )
        if (overflowed) {
            Button(
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.textButtonColors().copy(containerColor = Color.Transparent),
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    modifier = Modifier.rotate(if (expanded) 0f else 180f),
                    painter = painterResource(R.drawable.keyboard_arrow_up_24px),
                    contentDescription = "expand",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (expanded) "收起" else "展开",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun VolumeItem(
    volume: Volume,
    readCompletedChapterIds: List<Int>,
    onClickChapter: (Int) -> Unit,
    volumesSize: Int
) {
    var expanded by remember { mutableStateOf(volumesSize <= 8) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .height(54.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = volume.volumeTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.width(12.dp))
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
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier.size(16.dp)
                    .rotate(if (expanded) 90f else 0f),
                painter = painterResource(id = R.drawable.arrow_forward_ios_24px),
                contentDescription = "expand"
            )
            Spacer(Modifier.width(12.dp))
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                volume.chapters.forEach {
                    Box(
                        modifier = Modifier
                            .clickable { onClickChapter(it.id) }
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = it.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp,
                            fontWeight =
                                if (readCompletedChapterIds.contains(it.id))
                                    FontWeight.Normal
                                else FontWeight.Bold,
                            color =
                                if (readCompletedChapterIds.contains(it.id))
                                    MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}