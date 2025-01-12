package indi.dmzz_yyhyy.lightnovelreader.ui.book

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.book.content.ContentScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.book.detail.DetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    onClickBackButton: () -> Unit,
    bookId: Int,
    chapterId: Int,
    cacheBook: (Int) -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navController = rememberNavController()
    var topBar : @Composable (TopAppBarScrollBehavior) -> Unit by remember { mutableStateOf(@Composable {}) }
    var lastIncomingChapterId by remember { mutableStateOf(-1) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedContent(topBar, label = "TopBarAnimated") { topBar ->
                topBar(scrollBehavior)
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Book.Detail.route
        ) {
            composable(
                route = Screen.Book.Detail.route,
                arguments = Screen.Book.Detail.navArguments
            ) {
                if (chapterId != lastIncomingChapterId) {
                    lastIncomingChapterId = chapterId
                    if (chapterId != -1)
                        navController.navigate(Screen.Book.Content.createRoute(chapterId))
                }
                DetailScreen(
                    paddingValues = paddingValues,
                    onClickBackButton = onClickBackButton,
                    onClickChapter = {
                        navController.navigate(Screen.Book.Content.createRoute(it))
                    },
                    topBar = { newTopBar -> topBar = newTopBar },
                    id = bookId,
                    cacheBook = cacheBook,
                    requestAddBookToBookshelf = requestAddBookToBookshelf,
                )
            }
            composable(
                route = Screen.Book.Content.route,
                arguments = Screen.Book.Content.navArguments
            ) { navBackStackEntry ->
                navBackStackEntry.arguments?.let { bundle ->
                    ContentScreen(
                        paddingValues = paddingValues,
                        onClickBackButton = {
                            navController.popBackStack()
                        },
                        topBar = { newTopBar ->
                            topBar = newTopBar
                        },
                        bookId = bookId,
                        chapterId = bundle.getInt("chapterId")
                    )
                }
            }
        }
    }
}