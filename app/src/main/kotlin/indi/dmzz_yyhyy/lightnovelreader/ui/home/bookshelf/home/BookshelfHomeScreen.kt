package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.EmptyPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfHomeScreen(
    init: () -> Unit,
    topBar: (@Composable (TopAppBarScrollBehavior, TopAppBarScrollBehavior) -> Unit) -> Unit,
    changePage: (Int) -> Unit,
    uiState: BookshelfHomeUiState
) {
    var pinnedBooksExpended by remember { mutableStateOf(false) }
    var allBooksExpended by remember { mutableStateOf(false) }
    topBar { enterAlwaysScrollBehavior, _ ->
        TopBar(
            scrollBehavior = enterAlwaysScrollBehavior,
            onClickSearch = {}
        )
    }
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        topBar { enterAlwaysScrollBehavior, _ ->
            init.invoke()
        }
    }
    Column {
        PrimaryTabRow(selectedTabIndex = uiState.selectedTabIndex) {
            uiState.bookshelfList.forEach { bookshelf ->
                Tab(
                    selected = uiState.selectedBookshelfId == bookshelf.id,
                    onClick = { changePage(bookshelf.id) },
                    text = { Text(text = bookshelf.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }
        AnimatedVisibility(
            visible = uiState.selectedBookshelf.allBookIds.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EmptyPage(
                painter = painterResource(R.drawable.bookmarks_90px),
                title = "没有内容",
                description = "单击“收藏”按钮，将书本加入此书架"
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.selectedBookshelf.pinnedBookIds.isNotEmpty())
                item {
                    CollapseGroupTitle(
                        icon = painterResource(R.drawable.keep_24px),
                        title = "已固定 (${uiState.selectedBookshelf.pinnedBookIds.size})",
                        expanded = pinnedBooksExpended,
                        onClickExpand = { pinnedBooksExpended = !pinnedBooksExpended }
                    )
                }
            if (pinnedBooksExpended) {
                items(uiState.selectedBookshelf.pinnedBookIds) {

                }
            }
            if (uiState.selectedBookshelf.allBookIds.isNotEmpty())
                item {
                    CollapseGroupTitle(
                        icon = painterResource(R.drawable.outline_bookmark_24px),
                        title = "全部 (${uiState.selectedBookshelf.pinnedBookIds.size})",
                        expanded = allBooksExpended,
                        onClickExpand = { allBooksExpended = !allBooksExpended }
                    )
                }
            if (allBooksExpended) {
                items(uiState.selectedBookshelf.allBookIds) {
                    uiState.bookMap[it]?.let { it1 -> BookRow(it1) }
                }
            }
        }
    }
}

@Composable
fun CollapseGroupTitle(
    icon: Painter,
    title: String,
    expanded: Boolean,
    onClickExpand: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = icon,
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(2f),
            text = title,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.W700,
            fontSize = 15.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        IconButton(onClickExpand) {
            Icon(
                modifier = Modifier.rotate(if (expanded) 180f else 0f),
                painter = painterResource(R.drawable.keyboard_arrow_up_24px),
                contentDescription = "expand"
            )
        }
    }
}

@Composable
fun BookRow(bookInformation: BookInformation) {
    val descriptionTextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W400
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Cover(
            width = 64.dp,
            height = 90.dp,
            url = bookInformation.coverUrl,
            rounded = 8.dp
        )
        Column(Modifier.weight(2f).padding(horizontal = 8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                    text = bookInformation.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    maxLines = 2,
                )
            }
            Box(Modifier.height(4.dp))
            Text(
                buildAnnotatedString {
                    append(bookInformation.title)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                        append(" · ")
                    }
                    append("${bookInformation.wordCount/1000}K 字")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                        append(" · ")
                    }
                    if (!bookInformation.isComplete)
                        append("更新: ${bookInformation.lastUpdated.year}-${bookInformation.lastUpdated.month}-${bookInformation.lastUpdated.dayOfMonth}")
                    else
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("已完结")
                        }
                },
                style = descriptionTextStyle,
                maxLines = 1
            )
            Text(
                text = bookInformation.description,
                style = descriptionTextStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onClickSearch: () -> Unit
) {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.nav_bookshelf),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(R.drawable.library_add_24px),
                    contentDescription = "create"
                )
            }
            IconButton(onClickSearch) {
                Icon(
                    painter = painterResource(R.drawable.search_24px),
                    contentDescription = "search"
                )
            }
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
        scrollBehavior = scrollBehavior,
    )
}