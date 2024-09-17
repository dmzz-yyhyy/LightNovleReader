package indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.ui.components.EmptyPage
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.expanded.ExpandedPageScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.expanded.ExpandedPageViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.home.ExplorationHomeScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.home.ExplorationHomeViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.search.ExplorationSearchScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.search.ExplorationSearchViewModel


val ExplorationScreenInfo = NavItem (
    route = Screen.Home.Exploration.route,
    drawable = R.drawable.animated_exploration,
    label = R.string.nav_exploration
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exploration(
    topBar: (@Composable () -> Unit) -> Unit,
    dialog: (@Composable () -> Unit) -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit,
    onClickBook: (Int) -> Unit,
    explorationViewModel: ExplorationViewModel = hiltViewModel(),
    explorationHomeViewModel: ExplorationHomeViewModel = hiltViewModel(),
    explorationSearchViewModel: ExplorationSearchViewModel = hiltViewModel(),
    expandedPageViewModel: ExpandedPageViewModel = hiltViewModel()
    ) {
    val navController = rememberNavController()
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        explorationViewModel.init()
    }
    AnimatedVisibility(
        visible = explorationViewModel.uiState.isOffLine,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        topBar {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nav_exploration),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                windowInsets =
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                )
            )
        }
        EmptyPage(
            painter = painterResource(R.drawable.wifi_off_90dp),
            title = stringResource(id = R.string.offline),
            description = stringResource(id = R.string.offline_desc)
        )
    }
    AnimatedVisibility(
        visible = !explorationViewModel.uiState.isOffLine,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        NavHost(navController, startDestination = Screen.Home.Exploration.Home.route) {
            composable(route = Screen.Home.Exploration.Home.route) {
                ExplorationHomeScreen(
                    topBar = topBar,
                    onClickExpand = { navController.navigate(Screen.Home.Exploration.Expanded.createRoute(it)) },
                    onClickBook = onClickBook,
                    uiState = explorationHomeViewModel.uiState,
                    init = { explorationHomeViewModel.init() },
                    changePage = { explorationHomeViewModel.changePage(it) },
                    onClickSearch = { navController.navigate(Screen.Home.Exploration.Search.route) }
                )
            }
            composable(route = Screen.Home.Exploration.Search.route) {
                ExplorationSearchScreen(
                    topBar = topBar,
                    requestAddBookToBookshelf = requestAddBookToBookshelf,
                    onCLickBack = { navController.popBackStack() },
                    init = explorationSearchViewModel::init,
                    onChangeSearchType = { explorationSearchViewModel.changeSearchType(it) },
                    onSearch = { explorationSearchViewModel.search(it) },
                    onClickDeleteHistory = { explorationSearchViewModel.deleteHistory(it) },
                    onClickClearAllHistory = explorationSearchViewModel::clearAllHistory,
                    onClickBook = onClickBook,
                    uiState = explorationSearchViewModel.uiState
                )
            }
            composable(
                route = Screen.Home.Exploration.Expanded.route,
                arguments = Screen.Home.Exploration.Expanded.navArguments
            ) { navBackStackEntry ->
                navBackStackEntry.arguments?.getString("expandedPageDataSourceId")?.let { expandedPageDataSourceId ->
                    ExpandedPageScreen(
                        topBar = topBar,
                        dialog = dialog,
                        expandedPageDataSourceId = expandedPageDataSourceId,
                        uiState = expandedPageViewModel.uiState,
                        init = { expandedPageViewModel.init(it) },
                        loadMore = expandedPageViewModel::loadMore,
                        requestAddBookToBookshelf = requestAddBookToBookshelf,
                        onClickBack = {
                            expandedPageViewModel.clear()
                            navController.popBackStack()
                        },
                        onClickBook = onClickBook
                    )
                }
            }
        }
    }
}

@Composable
fun ExplorationBookCard(
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    allBookshelfBookIds: List<Int>,
    requestAddBookToBookshelf: (Int) -> Unit,
    onClickBook: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .height(125.dp)
            .clickable {
                onClickBook(bookInformation.id)
            }
    ) {
        Cover(
            width = 82.dp,
            height = 125.dp,
            url = bookInformation.coverUrl,
            rounded = 8.dp
        )
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 2.dp, 14.dp, 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(2f),
                    text = bookInformation.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W700,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                IconButton(
                    onClick = { requestAddBookToBookshelf(bookInformation.id) },
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        painter =
                        if (!allBookshelfBookIds.contains(bookInformation.id))
                            painterResource(R.drawable.outline_bookmark_24px)
                        else painterResource(R.drawable.filled_bookmark_24px),
                        contentDescription = "mark",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Text(
                text = stringResource(
                    id = R.string.book_info_detailed,
                    bookInformation.author,
                    bookInformation.publishingHouse,
                    bookInformation.lastUpdated.year,
                    bookInformation.lastUpdated.monthValue,
                    bookInformation.lastUpdated.dayOfMonth,
                    bookInformation.wordCount,
                    if (bookInformation.isComplete) stringResource(R.string.book_completed)
                    else stringResource(R.string.book_ongoing),
                    bookInformation.description.trim()
                ),
                style = MaterialTheme.typography.labelLarge,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}