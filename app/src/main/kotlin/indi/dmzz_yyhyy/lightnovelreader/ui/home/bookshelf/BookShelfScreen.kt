package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home.BookshelfHomeScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home.BookshelfHomeViewModel

val BookshelfScreenInfo = NavItem (
    route = Screen.Home.Bookshelf.route,
    drawable = R.drawable.animated_bookshelf,
    label = R.string.nav_bookshelf
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShelfScreen(
    topBar: (@Composable (TopAppBarScrollBehavior, TopAppBarScrollBehavior) -> Unit) -> Unit,
    bookshelfHomeViewModel: BookshelfHomeViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Home.Bookshelf.Home.route) {
        composable(route = Screen.Home.Bookshelf.Home.route) {
            BookshelfHomeScreen(
                topBar = topBar,
                init = bookshelfHomeViewModel::init,
                changePage = bookshelfHomeViewModel::changePage,
                uiState = bookshelfHomeViewModel.uiState
            )
        }
    }
}