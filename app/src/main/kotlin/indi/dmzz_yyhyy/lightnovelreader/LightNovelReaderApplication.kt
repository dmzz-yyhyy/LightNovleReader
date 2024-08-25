package indi.dmzz_yyhyy.lightnovelreader

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfSortType
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class LightNovelReaderApplication : Application(), Configuration.Provider {
    @Inject lateinit var bookshelfRepository: BookshelfRepository
    @Inject lateinit var workerFactory: HiltWorkerFactory
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        coroutineScope.launch(Dispatchers.IO) {
            if (bookshelfRepository.getAllBookshelfIds().isEmpty())
                bookshelfRepository.crateBookShelf(
                    name = "已收藏",
                    sortType = BookshelfSortType.Default,
                    autoCache = false,
                    systemUpdateReminder = false
                )
        }
    }

    override val workManagerConfiguration: Configuration
        get()  =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}