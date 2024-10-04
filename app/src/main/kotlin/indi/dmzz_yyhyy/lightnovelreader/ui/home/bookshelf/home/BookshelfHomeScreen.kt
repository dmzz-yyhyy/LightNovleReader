package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.ui.components.AnimatedText
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.EmptyPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfHomeScreen(
    init: () -> Unit,
    topBar: (@Composable () -> Unit) -> Unit,
    changePage: (Int) -> Unit,
    changeBookSelectState: (Int) -> Unit,
    uiState: BookshelfHomeUiState,
    onClickCreat: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickBook: (Int) -> Unit,
    onClickEnableSelectMode: () -> Unit,
    onClickDisableSelectMode: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickPin: () -> Unit,
    onClickRemove: () -> Unit,
    saveAllBookshelfJsonData: (Uri) -> Unit,
    saveBookshelfJsonData: (Uri) -> Unit,
    importBookshelf: (Uri) -> Unit,
    clearToast: () -> Unit
) {
    val context = LocalContext.current
    val enterAlwaysScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val animatedBackgroundColor by animateColorAsState(
        if (!uiState.selectMode) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainer
    )
    val saveAllBookshelfLauncher = launcher(saveAllBookshelfJsonData)
    val saveThisBookshelfLauncher = launcher(saveBookshelfJsonData)
    val importBookshelfLauncher = launcher(importBookshelf)
    var updatedBooksExpended by remember { mutableStateOf(true) }
    var pinnedBooksExpended by remember { mutableStateOf(true) }
    var allBooksExpended by remember { mutableStateOf(true) }
    topBar {
        TopBar(
            scrollBehavior = enterAlwaysScrollBehavior,
            backgroundColor = animatedBackgroundColor,
            selectMode = uiState.selectMode,
            onClickCreat = onClickCreat,
            onClickSearch = {},
            onClickEdit = { onClickEdit(uiState.selectedBookshelfId) },
            onClickDisableSelectMode = onClickDisableSelectMode,
            onClickSelectAll = onClickSelectAll,
            onClickPin = onClickPin,
            onClickRemove = onClickRemove,
            onClickSaveThisBookshelf = { createBookshelfDataFile(uiState.selectedBookshelf.name, saveThisBookshelfLauncher) },
            onClickSaveAllBookshelf = { createBookshelfDataFile("bookshelves", saveAllBookshelfLauncher) },
            onClickImportBookshelf = { selectBookshelfDataFile(importBookshelfLauncher) }
        )
    }
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        init.invoke()
    }
    LaunchedEffect(uiState.toast) {
        if (uiState.toast.isEmpty()) return@LaunchedEffect
        Toast.makeText(context, uiState.toast, Toast.LENGTH_SHORT).show()
        clearToast()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(animatedBackgroundColor)
            }
    ) {
        if (uiState.bookshelfList.size > 4) {
            ScrollableTabRow(
                containerColor = animatedBackgroundColor,
                selectedTabIndex = uiState.selectedTabIndex,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[uiState.selectedTabIndex])
                            .height(4.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                uiState.bookshelfList.forEach { bookshelf ->
                    Tab(
                        selected = uiState.selectedBookshelfId == bookshelf.id,
                        onClick = { if (!uiState.selectMode) changePage(bookshelf.id) },
                        text = {
                            Text(
                                text = bookshelf.name,
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
        else {
            PrimaryTabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                containerColor = animatedBackgroundColor
            ) {
                uiState.bookshelfList.forEach { bookshelf ->
                    Tab(
                        selected = uiState.selectedBookshelfId == bookshelf.id,
                        onClick = { if (!uiState.selectMode) changePage(bookshelf.id) },
                        text = { Text(text = bookshelf.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    )
                }
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
            modifier = Modifier.fillMaxWidth().nestedScroll(enterAlwaysScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.selectedBookshelf.updatedBookIds.isNotEmpty())
                item {
                    CollapseGroupTitle(
                        modifier = Modifier.animateItem(),
                        icon = painterResource(R.drawable.keep_24px),
                        title = "已更新 (${uiState.selectedBookshelf.updatedBookIds.size})",
                        expanded = updatedBooksExpended,
                        onClickExpand = { updatedBooksExpended = !updatedBooksExpended }
                    )
                }
            if (updatedBooksExpended && !uiState.selectMode) {
                items(uiState.selectedBookshelf.updatedBookIds) { updatedBookId ->
                    uiState.bookInformationMap[updatedBookId]?.let {
                        UpdatedBookRow(
                            modifier = Modifier.animateItem(),
                            bookInformation = it,
                            lastChapterTitle = uiState.bookLastChapterTitleMap[updatedBookId] ?: "",
                            selected = uiState.selectedBookIds.contains(it.id),
                            onClick = { onClickBook(it.id) },
                            onLongPress = {}
                        )
                    }
                }
            }
            if (uiState.selectedBookshelf.pinnedBookIds.isNotEmpty() && !uiState.selectMode)
                item {
                    CollapseGroupTitle(
                        modifier = Modifier.animateItem(),
                        icon = painterResource(R.drawable.keep_24px),
                        title = "已固定 (${uiState.selectedBookshelf.pinnedBookIds.size})",
                        expanded = pinnedBooksExpended,
                        onClickExpand = { pinnedBooksExpended = !pinnedBooksExpended }
                    )
                }
            if (pinnedBooksExpended) {
                items(uiState.selectedBookshelf.pinnedBookIds) { pinnedBookId ->
                    uiState.bookInformationMap[pinnedBookId]?.let { bookInformation ->
                        BookRow(
                            modifier = Modifier.animateItem(),
                            bookInformation = bookInformation,
                            selected = uiState.selectedBookIds.contains(bookInformation.id),
                            onClick = {
                                if (!uiState.selectMode)
                                    onClickBook(bookInformation.id)
                                else changeBookSelectState(bookInformation.id)
                            },
                            onLongPress = {
                                onClickEnableSelectMode.invoke()
                                changeBookSelectState(bookInformation.id)
                            }
                        )
                    }
                }
            }
            if (uiState.selectedBookshelf.allBookIds.isNotEmpty())
                item {
                    CollapseGroupTitle(
                        modifier = Modifier.animateItem(),
                        icon = painterResource(R.drawable.outline_bookmark_24px),
                        title = "全部 (${uiState.selectedBookshelf.allBookIds.size})",
                        expanded = allBooksExpended,
                        onClickExpand = { allBooksExpended = !allBooksExpended }
                    )
                }
            if (allBooksExpended) {
                items(
                    uiState.selectedBookshelf.allBookIds,
                ) { bookId ->
                    uiState.bookInformationMap[bookId]?.let {
                        BookRow(
                            modifier = Modifier.animateItem(),
                            bookInformation = it,
                            selected = uiState.selectedBookIds.contains(it.id),
                            onClick = {
                                if (!uiState.selectMode)
                                    onClickBook(it.id)
                                else changeBookSelectState(it.id)
                            },
                            onLongPress = {
                                onClickEnableSelectMode.invoke()
                                changeBookSelectState(it.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollapseGroupTitle(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    expanded: Boolean,
    onClickExpand: () -> Unit
) {
    Row(
        modifier = modifier
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
        AnimatedText(
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
                modifier = Modifier.rotate(if (expanded) 0f else 180f),
                painter = painterResource(R.drawable.keyboard_arrow_up_24px),
                contentDescription = "expand"
            )
        }
    }
}

@Composable
fun BookRow(
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    selected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val descriptionTextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 13.sp,
        lineHeight = 12.5.sp,
        fontWeight = FontWeight.W400
    )
    BasicBookRow(
        modifier = modifier,
        bookInformation = bookInformation,
        selected = selected,
        onClick = onClick,
        onLongPress = onLongPress
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(descriptionTextStyle.toSpanStyle()) {
                    append(bookInformation.author)
                }
                withStyle(
                    style = descriptionTextStyle.copy(fontWeight = FontWeight.W900).toSpanStyle()
                ) {
                    append(" · ")
                }
                withStyle(descriptionTextStyle.toSpanStyle()) {
                    append(bookInformation.publishingHouse)
                }
            },
            style = descriptionTextStyle,
            maxLines = 1
        )
        Text(
            text = buildAnnotatedString {
                withStyle(descriptionTextStyle.toSpanStyle()) {
                    append("${bookInformation.wordCount / 1000}K 字")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.W900)) {
                    append(" · ")
                }
                if (!bookInformation.isComplete)
                    withStyle(descriptionTextStyle.toSpanStyle()) {
                        append("更新: ${bookInformation.lastUpdated.year}-${bookInformation.lastUpdated.monthValue}-${bookInformation.lastUpdated.dayOfMonth}")
                    }
                else
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("已完结")
                    }
            },
            style = descriptionTextStyle,
            maxLines = 1
        )
        Text(
            text = bookInformation.tags.joinToString(" "),
            style = descriptionTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = bookInformation.description.trim(),
            style = descriptionTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun UpdatedBookRow(
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    lastChapterTitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    val descriptionTextStyle = MaterialTheme.typography.labelLarge.copy(
        fontSize = 12.sp,
        lineHeight = 12.5.sp,
        fontWeight = FontWeight.W400
    )
    val primary = MaterialTheme.colorScheme.primary
    BasicBookRow(
        modifier = modifier,
        bookInformation = bookInformation,
        selected = selected,
        onClick = onClick,
        onLongPress = onLongPress
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(descriptionTextStyle.toSpanStyle()) {
                    append(bookInformation.author)
                }
                withStyle(
                    style = descriptionTextStyle.copy(fontWeight = FontWeight.W900).toSpanStyle()
                ) {
                    append(" · ")
                }
                withStyle(descriptionTextStyle.toSpanStyle()) {
                    append(bookInformation.publishingHouse)
                }
            },
            style = descriptionTextStyle,
            maxLines = 1
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = descriptionTextStyle.copy(fontWeight = FontWeight.W900).toSpanStyle()
                ) {
                    append("更新至 ")
                }
                withStyle(
                    style = descriptionTextStyle.copy(fontWeight = FontWeight.W900, color = primary).toSpanStyle()
                ) {
                    append(lastChapterTitle)
                }
            },
            style = descriptionTextStyle,
            maxLines = 1
        )
        Text(
            text = bookInformation.tags.joinToString(" "),
            style = descriptionTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicBookRow(
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    selected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    description: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = modifier
            .height(125.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
    ) {
        Box(Modifier.size(82.dp, 125.dp).clip(RoundedCornerShape(8.dp))) {
            Cover(
                width = 82.dp,
                height = 125.dp,
                url = bookInformation.coverUrl,
                rounded = 8.dp
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f))
                ) {
                    val color = MaterialTheme.colorScheme.primary
                    Canvas(Modifier.align(Alignment.Center).size(36.dp)) {
                        drawCircle(
                            color = color,
                            radius = 18.dp.toPx()
                        )
                    }
                    Icon(
                        modifier = Modifier.align(Alignment.Center).size(22.dp),
                        painter = painterResource(R.drawable.check_24px),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                }
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 2.dp, 14.dp, 5.dp)
        ) {
            Text(
                text = bookInformation.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W800,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                maxLines = 2
            )
            description.invoke(this@Column)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    backgroundColor: Color,
    selectMode: Boolean,
    onClickCreat: () -> Unit,
    onClickSearch: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDisableSelectMode: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickPin: () -> Unit,
    onClickRemove: () -> Unit,
    onClickSaveThisBookshelf: () -> Unit,
    onClickSaveAllBookshelf: () -> Unit,
    onClickImportBookshelf: () -> Unit
) {
    val localDensity = LocalDensity.current
    var mainMenuExpended by remember { mutableStateOf(false) }
    var exportImportMenuExpended by remember { mutableStateOf(false) }
    var mainMenuWidth by remember { mutableStateOf(0.dp) }
    var mainMenuItemHeight by remember { mutableStateOf(0.dp) }
    var exportImportMenuWidth by remember { mutableStateOf(0.dp) }
    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Box(Modifier.align(Alignment.TopEnd)) {
            DropdownMenu(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        with(localDensity) {
                            mainMenuWidth = layoutCoordinates.size.width.toDp()
                            mainMenuItemHeight = layoutCoordinates.size.height.toDp().div(4)
                        }
                    },
                offset = DpOffset(0.dp, (-1).dp),
                expanded = mainMenuExpended,
                onDismissRequest = { mainMenuExpended = false }) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "新建书架",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    onClick = onClickCreat
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "编辑此书架",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    onClick = onClickEdit
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "导入和导出...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_right_24px),
                            contentDescription = null
                        )
                    },
                    onClick = { exportImportMenuExpended = true }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "排序方式...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_right_24px),
                            contentDescription = null
                        )
                    },
                    onClick = { }
                )
            }
        }
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = exportImportMenuWidth + mainMenuWidth + 12.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            DropdownMenu(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        with(localDensity) {
                            exportImportMenuWidth = layoutCoordinates.size.width.toDp()
                        }
                    },
                offset = DpOffset(0.dp, mainMenuItemHeight.times(3.5f)),
                expanded = exportImportMenuExpended,
                onDismissRequest = { exportImportMenuExpended = false }) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "导出为 .lnr",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    onClick = {
                        onClickSaveThisBookshelf()
                        exportImportMenuExpended = false
                        mainMenuExpended = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "导出全部为 .lnr",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    onClick = {
                        onClickSaveAllBookshelf()
                        exportImportMenuExpended = false
                        mainMenuExpended = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "从文件导入",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.W400
                        )
                    },
                    onClick = {
                        onClickImportBookshelf()
                        exportImportMenuExpended = false
                        mainMenuExpended = false
                    }
                )
            }
        }
    }

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
        navigationIcon = {
            AnimatedVisibility(selectMode) {
                IconButton(onClickDisableSelectMode) {
                    Icon(
                        painter = painterResource(R.drawable.cancel_24px),
                        contentDescription = "cancel"
                    )
                }
            }
        },
        actions = {
            IconButton(
                if (!selectMode) {
                    scrollBehavior.state.heightOffset = 0f
                    onClickCreat
                }
                else onClickSelectAll
            ) {
                Icon(
                    painter = if (!selectMode) painterResource(R.drawable.library_add_24px) else painterResource(R.drawable.select_all_24px),
                    contentDescription = if (!selectMode) "create" else "select all"
                )
            }
            IconButton(if (!selectMode) onClickSearch else onClickPin) {
                Icon(
                    painter = if (!selectMode) painterResource(R.drawable.search_24px) else painterResource(R.drawable.keep_24px),
                    contentDescription = if (!selectMode) "search" else "pin"
                )
            }
            IconButton(if (!selectMode) { { mainMenuExpended = true } } else onClickRemove) {
                Icon(
                    painter = if (!selectMode) painterResource(R.drawable.more_vert_24px) else painterResource(R.drawable.bookmark_remove_24px),
                    contentDescription = if (!selectMode) stringResource(R.string.more) else "remove"
                )
            }
        },
        windowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        ),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor
        )
    )
}

fun createBookshelfDataFile(fileName: String, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val initUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Document")
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri)
        putExtra(Intent.EXTRA_TITLE, "$fileName.lnr")
    }
    launcher.launch(Intent.createChooser(intent, "选择一位置"))
}

@Composable
fun launcher(block: (Uri) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            activityResult.data?.data?.let { uri ->
                block(uri)
            }
        }
    }
}

fun selectBookshelfDataFile(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val initUri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Document")
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, initUri)
    }
    launcher.launch(Intent.createChooser(intent, "选择数据文件"))
}