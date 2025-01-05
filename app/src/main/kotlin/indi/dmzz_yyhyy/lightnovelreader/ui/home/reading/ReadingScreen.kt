package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.EmptyPage
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Loading
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.utils.formTime

val ReadingScreenInfo = NavItem(
    route = Screen.Home.Reading.route,
    drawable = R.drawable.animated_book,
    label = R.string.nav_reading
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingScreen(
    onClickBook: (Int) -> Unit,
    onClickContinueReading: (Int, Int) -> Unit,
    onClickJumpToExploration: () -> Unit,
    topBar: (@Composable () -> Unit) -> Unit,
    viewModel: ReadingViewModel = hiltViewModel()
) {
    val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val readingBooks = viewModel.uiState.recentReadingBooks.reversed()
    topBar {
        TopBar(pinnedScrollBehavior)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.update()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .nestedScroll(pinnedScrollBehavior.nestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = "上次阅读",
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        item {
            AnimatedVisibility(
                visible =  !viewModel.uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ReadingHeaderCard(
                    book = readingBooks[0],
                    onClickBook = onClickBook,
                    onClickContinueReading = onClickContinueReading
                )
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = stringResource(
                        R.string.recent_reads, readingBooks.size,
                    ),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        items(readingBooks) {
            AnimatedVisibility(
                visible =  !viewModel.uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ReadingBookCard(
                    book = it,
                    onClick = {
                        onClickBook(it.id)
                    }
                )
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
        }
    }
    AnimatedVisibility(
        visible =  viewModel.uiState.isLoading && viewModel.uiState.recentReadingBooks.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        EmptyPage(
            painter = painterResource(R.drawable.empty_90dp),
            title = stringResource(id = R.string.nothing_here),
            description = stringResource(id = R.string.nothing_here_desc_reading),
            button = {
                Button(
                    onClick = onClickJumpToExploration
                ) {
                    Text(
                        text = stringResource(id = R.string.navigate_to_exploration),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.W500,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )
    }
    AnimatedVisibility(
        visible =  viewModel.uiState.isLoading && viewModel.uiState.recentReadingBooks.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Loading()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
                Text(
                    text = stringResource(R.string.nav_reading),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
        actions = {
            IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more)
                    )
                }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReadingBookCard(
    book: ReadingBook,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.height(144.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.combinedClickable(
                onClick = onClick
            ).padding(4.dp),
        ) {
            Cover(
                width = 94.dp,
                height = 142.dp,
                url = book.coverUrl,
                rounded = 8.dp
            )
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                val titleLineHeight = 20.sp
                Text(
                    modifier = Modifier.height(
                        with(LocalDensity.current) { (titleLineHeight * 2.2f).toDp() }
                    ).wrapContentHeight(Alignment.CenterVertically),
                    text = book.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = titleLineHeight,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.author,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 20.sp,
                        fontSize = 14.sp,
                    )
                }
                Text(
                    text = book.description.trim(),
                    maxLines = 2,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 18.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row {
                        Icon(
                            modifier = Modifier.size(14.dp)
                                .align(Alignment.CenterVertically)
                                .padding(top = 2.dp, end = 2.dp),
                            painter = painterResource(id = R.drawable.outline_schedule_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = formTime(book.lastReadTime),
                            modifier = Modifier.align(Alignment.CenterVertically),
                            fontSize = 13.sp,
                            lineHeight = 14.sp
                        )
                    }
                    Text(
                        fontSize = 13.sp,
                        lineHeight = 14.sp,
                        text = "• 已读 ${(book.readingProgress * 100).toInt()}%"
                    )
                    Text(
                        text = "• ${(book.totalReadTime) / 60} 分钟",
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 13.sp,
                        lineHeight = 14.sp
                    )
                }

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { book.readingProgress }
                )
            }
        }
    }
}

@Composable
private fun ReadingHeaderCard(
    book: ReadingBook,
    onClickContinueReading: (Int, Int) -> Unit,
    onClickBook: (Int) -> Unit,
) {
    Box {
        Row(
            modifier = Modifier.fillMaxWidth().height(178.dp)
                .padding(4.dp),
        ) {
            Box {
                Cover(
                    height = 178.dp,
                    width = 122.dp,
                    url = book.coverUrl,
                    rounded = 8.dp
                )
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(id = R.drawable.filled_menu_book_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "上次阅读 | ${formTime(book.lastReadTime)}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                    )
                }
                val titleLineHeight = 24.sp
                Text(
                    modifier = Modifier.height(
                        with(LocalDensity.current) { (titleLineHeight * 2.2f).toDp() }
                    ).wrapContentHeight(Alignment.CenterVertically),
                    text = book.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    lineHeight = titleLineHeight,
                )
                Text(
                    text = "章节",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                )
                Text(
                    text = book.lastReadChapterTitle,
                    maxLines = 1,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { onClickBook(book.id) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "详情",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Button(onClick = { onClickContinueReading(book.id, book.lastReadChapterId) }) {
                        Text(
                            text = "继续上次阅读",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        }
    }
}