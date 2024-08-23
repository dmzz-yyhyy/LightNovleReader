package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.ui.book.BookScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.AddBookToBookshelfDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.UpdatesAvailableDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.home.HomeScreen

@Composable
fun LightNovelReaderApp(
    viewModel: LightNovelReaderViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    onClickInstallUpdate: () -> Unit = {
        viewModel.installUpdate(
            url = viewModel.updateDialogUiState.downloadUrl,
            version = viewModel.updateDialogUiState.versionName,
            size = viewModel.updateDialogUiState.downloadSize.toLong(),
            context = context
        )
    },
) {
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.autoCheckUpdate()
    }
    LaunchedEffect(viewModel.updateDialogUiState.toast) {
        if (viewModel.updateDialogUiState.toast.isBlank()) return@LaunchedEffect
        Toast.makeText(context, viewModel.updateDialogUiState.toast, Toast.LENGTH_SHORT).show()
        viewModel.clearToast()
    }
    val navController = rememberNavController()

    AnimatedVisibility(visible = viewModel.updateDialogUiState.visible) {
        UpdatesAvailableDialog(
            onDismissRequest = viewModel::onDismissUpdateRequest,
            onConfirmation = onClickInstallUpdate,
            newVersionCode = viewModel.updateDialogUiState.versionCode,
            newVersionName = viewModel.updateDialogUiState.versionName,
            contentMarkdown = viewModel.updateDialogUiState.releaseNotes,
            downloadSize = viewModel.updateDialogUiState.downloadSize,
        )
    }
    AnimatedVisibility(visible = viewModel.addToBookshelfDialogUiState.visible) {
        AddBookToBookshelfDialog(
            onDismissRequest = viewModel::onDismissAddToBookshelfRequest,
            onConfirmation = viewModel::addBookToBookshelf,
            onSelectBookshelf = viewModel::onSelectBookshelf,
            onDeselectBookshelf = viewModel::onDeselectBookshelf,
            allBookshelf = viewModel.addToBookshelfDialogUiState.allBookShelf,
            selectedBookshelfIds = viewModel.addToBookshelfDialogUiState.selectedBookshelfIds
        )
    }
    LightNovelReaderNavHost(
        navController = navController,
        checkUpdate = viewModel::checkUpdate,
        requestAddBookToBookshelf = viewModel::requestAddBookToBookshelf,
    )
}

@Composable
fun LightNovelReaderNavHost(
    navController: NavHostController,
    checkUpdate: () -> Unit,
    requestAddBookToBookshelf: (Int) -> Unit,
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
                checkUpdate = checkUpdate,
                requestAddBookToBookshelf = requestAddBookToBookshelf
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
                    requestAddBookToBookshelf = requestAddBookToBookshelf
            ) }
        }
    }
}