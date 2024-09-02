package indi.dmzz_yyhyy.lightnovelreader.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.book.BookScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.AddBookToBookshelfDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.BaseDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SettingsSliderEntry
import indi.dmzz_yyhyy.lightnovelreader.ui.components.SliderDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.components.UpdatesAvailableDialog
import indi.dmzz_yyhyy.lightnovelreader.ui.home.HomeScreen
import kotlin.math.roundToInt

@Composable
fun LightNovelReaderApp(
    viewModel: LightNovelReaderViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
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
        val releaseNotes = viewModel.updateDialogUiState.release.releaseNotes ?: ""
        val downloadUrl = viewModel.updateDialogUiState.release.downloadUrl ?: ""
        val version = viewModel.updateDialogUiState.release.version ?: -1
        val versionName = viewModel.updateDialogUiState.release.versionName ?: ""
        val checksum = viewModel.updateDialogUiState.release.checksum ?: ""
        val downloadSize = viewModel.updateDialogUiState.release.downloadSize?.toLong() ?: -1
        UpdatesAvailableDialog(
            onDismissRequest = viewModel::onDismissUpdateRequest,
            onConfirmation = { viewModel.downloadUpdate(
                url = downloadUrl,
                version = versionName,
                checksum = checksum,
                context = context
            ) },
            newVersionCode = version,
            newVersionName = versionName,
            contentMarkdown = releaseNotes,
            downloadSize = downloadSize.toDouble(),
            downloadUrl = downloadUrl
        )
    }
    AnimatedVisibility(visible = viewModel.addToBookshelfDialogUiState.visible) {
        AddBookToBookshelfDialog(
            onDismissRequest = viewModel::onDismissAddToBookshelfRequest,
            onConfirmation = viewModel::processAddToBookshelfRequest,
            onSelectBookshelf = viewModel::onSelectBookshelf,
            onDeselectBookshelf = viewModel::onDeselectBookshelf,
            allBookshelf = viewModel.addToBookshelfDialogUiState.allBookShelf,
            selectedBookshelfIds = viewModel.addToBookshelfDialogUiState.selectedBookshelfIds
        )
    }
    LightNovelReaderNavHost(
        navController = navController,
        checkUpdate = viewModel::checkUpdate,
        cacheBook = viewModel::cacheBook,
        requestAddBookToBookshelf = viewModel::requestAddBookToBookshelf,
    )
}

@Composable
fun LightNovelReaderNavHost(
    navController: NavHostController,
    checkUpdate: () -> Unit,
    cacheBook: (Int) -> Unit,
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
                    cacheBook = cacheBook,
                    requestAddBookToBookshelf = requestAddBookToBookshelf
            ) }
        }
    }
}