package indi.dmzz_yyhyy.lightnovelreader.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdatesAvailableDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.book.BookScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.HomeScreen

@Composable
fun LightNovelReaderApp(
    viewModel: LightNovelReaderViewModel = hiltViewModel(),
    onClickInstallUpdate: () -> Unit = {
        viewModel.installUpdate(viewModel.uiState.downloadUrl)
    },
) {

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.checkUpdates()
    }
    val navController = rememberNavController()
    var showUpdateDialog by remember { mutableStateOf(true) }
    AnimatedVisibility(visible = viewModel.uiState.visible) {

        if (showUpdateDialog) UpdatesAvailableDialog(
            onDismissRequest = {
                showUpdateDialog = false
            },
            onConfirmation = {
                onClickInstallUpdate()
            },
            onIgnore = {},
            newVersion = viewModel.uiState.versionName,
            contentMarkdown = viewModel.uiState.releaseNotes
        )
    }
    LightNovelReaderNavHost(navController)
}

@Composable
fun LightNovelReaderNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onClickBook = {
                    navController.navigate(Screen.Book.createRoute(it))
                },
                onClickContinueReading = { bookId, chapterId ->
                    navController.navigate(Screen.Book.createRoute(bookId, chapterId))
                }
            )
        }
        composable(
            route = Screen.Book.route,
            arguments = Screen.Book.navArguments
        ) {
            it.arguments?.let { it1 ->
                BookScreen(
                    onClickBackButton = { navController.popBackStack() },
                    bookId = it1.getInt("bookId"),
                    chapterId = it1.getInt("chapterId"),
            ) }
        }
    }
}