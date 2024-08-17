package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdatesAvailableDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.book.BookScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.HomeScreen

@Composable
fun LightNovelReaderApp(
    viewModel: LightNovelReaderViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    onClickInstallUpdate: () -> Unit = {
        viewModel.installUpdate(
            url = viewModel.uiState.downloadUrl,
            version = viewModel.uiState.versionName,
            context = context
        )
    },
) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.checkUpdates()
    }
    val navController = rememberNavController()

    AnimatedVisibility(visible = viewModel.uiState.visible) {
        UpdatesAvailableDialog(
            onDismissRequest = viewModel::onDismissRequest,
            onConfirmation = onClickInstallUpdate,
            onIgnore = viewModel::onDismissRequest,
            newVersion = viewModel.uiState.versionName,
            contentMarkdown = viewModel.uiState.releaseNotes,
            downloadSize = viewModel.uiState.downloadSize
        )
    }
    LightNovelReaderNavHost(navController, viewModel::checkUpdates)
}

@Composable
fun LightNovelReaderNavHost(
    navController: NavHostController,
    checkUpdate: () -> Unit
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
                },
                checkUpdate = checkUpdate
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