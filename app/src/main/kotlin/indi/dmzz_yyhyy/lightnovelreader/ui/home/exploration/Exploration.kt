package indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.EmptyPage
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.expanded.ExpandedPageScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.expanded.ExpandedPageViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.home.ExplorationHomeScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.home.ExplorationHomeViewModel
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.search.ExplorationSearchScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.search.ExplorationSearchViewModel
import kotlinx.coroutines.launch


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
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val rememberPullToRefreshState = rememberPullToRefreshState()
    LaunchedEffect(explorationViewModel.uiState.isOffLine) {
        if (explorationViewModel.uiState.isOffLine)
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
    }
    AnimatedVisibility(
        visible = explorationViewModel.uiState.isOffLine,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = explorationViewModel.uiState.isRefreshing,
            state = rememberPullToRefreshState,
            onRefresh = {
                explorationViewModel.refresh()
                scope.launch {
                    rememberPullToRefreshState.animateToHidden()
                }
            }
        ) {
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                item {
                    EmptyPage(
                        painter = painterResource(R.drawable.wifi_off_90dp),
                        title = stringResource(id = R.string.offline),
                        description = stringResource(id = R.string.offline_desc)
                    )
                }
            }
        }
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
                    onClickSearch = { navController.navigate(Screen.Home.Exploration.Search.route) },
                    refresh = explorationHomeViewModel::refresh
                )
            }
            composable(route = Screen.Home.Exploration.Search.route) {
                ExplorationSearchScreen(
                    topBar = topBar,
                    requestAddBookToBookshelf = requestAddBookToBookshelf,
                    onClickBack = { navController.popBackStack() },
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
                        onClickBook = onClickBook,
                        refresh = expandedPageViewModel::refresh
                    )
                }
            }
        }
    }
}