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
import indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.edit.EditBookshelfScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.edit.EditBookshelfViewModel
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
    dialog: (@Composable () -> Unit) -> Unit,
    onClickBook: (Int) -> Unit,
    bookshelfHomeViewModel: BookshelfHomeViewModel = hiltViewModel(),
    editBookshelfViewModel: EditBookshelfViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Home.Bookshelf.Home.route) {
        composable(route = Screen.Home.Bookshelf.Home.route) {
            BookshelfHomeScreen(
                topBar = topBar,
                init = bookshelfHomeViewModel::init,
                changePage = bookshelfHomeViewModel::changePage,
                changeBookSelectState = bookshelfHomeViewModel::changeBookSelectState,
                uiState = bookshelfHomeViewModel.uiState,
                onClickCreat = {
                    navController.navigate(
                        Screen.Home.Bookshelf.Edit.createRoute(
                            "新建书架",
                            -1
                        )
                    )
                },
                onClickEdit = {
                    navController.navigate(
                        Screen.Home.Bookshelf.Edit.createRoute(
                            "编辑书架",
                            it
                        )
                    )
                },
                onClickBook = onClickBook,
                onClickEnableSelectMode = bookshelfHomeViewModel::enableSelectMode,
                onClickDisableSelectMode = bookshelfHomeViewModel::disableSelectMode,
                onClickSelectAll = bookshelfHomeViewModel::selectAllBooks,
                onClickPin = bookshelfHomeViewModel::pinSelectedBooks,
                onClickRemove = bookshelfHomeViewModel::removeSelectedBooks
            )
        }
        composable(
            route = Screen.Home.Bookshelf.Edit.route,
            arguments = Screen.Home.Bookshelf.Edit.navArguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.let {
                EditBookshelfScreen(
                    title = it.getString("title") ?: "",
                    bookshelfId = it.getInt("id"),
                    bookshelf = editBookshelfViewModel.uiState,
                    topBar = topBar,
                    dialog = dialog,
                    inti = editBookshelfViewModel::init,
                    onClickBack = { navController.popBackStack() },
                    onClickSave = {
                        navController.popBackStack()
                        editBookshelfViewModel.save()
                    },
                    onClickDelete = {
                        navController.popBackStack()
                        editBookshelfViewModel.delete()
                    },
                    onNameChange = editBookshelfViewModel::onNameChange,
                    onAutoCacheChange = editBookshelfViewModel::onAutoCacheChange,
                    onSystemUpdateReminderChange = editBookshelfViewModel::onSystemUpdateReminderChange,
                )
            }
        }
    }
}