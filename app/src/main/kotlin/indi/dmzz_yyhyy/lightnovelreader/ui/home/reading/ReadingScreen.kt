package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.update()
    }
    topBar {
        TopBar(pinnedScrollBehavior)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .nestedScroll(pinnedScrollBehavior.nestedScrollConnection),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                text = stringResource(
                    R.string.recent_reads, readingBooks.size
                ),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            AnimatedVisibility(
                visible =  !viewModel.uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LargeBookCard(
                    book = readingBooks[0],
                    onClickContinueReading = onClickContinueReading
                )
            }
        }
        items(readingBooks) {
            AnimatedVisibility(
                visible =  !viewModel.uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SimpleBookCard(
                    book =  it,
                    onClicked = {
                        onClickBook(it.id)
                    }
                )
            }
        }
    }
    AnimatedVisibility(
        visible =  viewModel.uiState.isLoading,
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more)
                    )
                }
        },
        windowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun SimpleBookCard(book: ReadingBook, onClicked: () -> Unit) {
    Row(Modifier
        .fillMaxWidth()
        .height(120.dp)
        .clickable(onClick = onClicked)
    ) {
        Cover(81.dp, 120.dp, book.coverUrl)
        Column(Modifier.fillMaxSize().padding(16.dp, 0.dp, 0.dp, 0.dp)) {
            Column(Modifier.fillMaxWidth().height(96.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 4.dp),
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.W600
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "作者: ${book.author} / 文库: ${book.publishingHouse}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = book.description.trim(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxHeight().width(259.dp).align(Alignment.CenterStart)) {
                    Icon(
                        modifier = Modifier.size(16.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.outline_schedule_24px),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = " ${formTime(book.lastReadTime)}"
                                + " · 读了${(book.totalReadTime / 60)}分钟"
                                + " · ${(book.readingProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.W400
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LargeBookCard(
    book: ReadingBook,
    onClickContinueReading: (Int, Int) -> Unit
) {
    Box(Modifier.padding(0.dp, 8.dp, 0.dp, 8.dp).fillMaxWidth().height(194.dp)) {
        Row {
            Cover(118.dp, 178.dp, book.coverUrl)
            Column(Modifier.padding(24.dp, 0.dp, 0.dp, 0.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth().height(66.dp),
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.W600
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    modifier = Modifier.fillMaxWidth().height(66.dp),
                    text = book.description.trim(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500
                    ),
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
                Button(onClick = { onClickContinueReading(book.id, book.lastReadChapterId) }) {
                    Text(
                        text = "继续阅读: ${book.lastReadChapterTitle}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.W700
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}