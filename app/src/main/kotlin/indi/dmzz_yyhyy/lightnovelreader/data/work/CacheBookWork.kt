package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.data.local.LocalBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource

@HiltWorker
class CacheBookWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localBookDataSource: LocalBookDataSource,
    private val webBookDataSource: WebBookDataSource
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val bookId = inputData.getInt("bookId", -1)
        if (bookId < 0) return Result.failure()
        webBookDataSource.getBookInformation(bookId)
            ?.let {
                if (it.isEmpty()) return Result.failure()
                localBookDataSource.updateBookInformation(it)
            }
        webBookDataSource.getBookVolumes(bookId)?.let { bookVolumes ->
            if (bookVolumes.volumes.isEmpty()) return Result.failure()
            localBookDataSource.updateBookVolumes(bookId, bookVolumes)
            bookVolumes.volumes.forEach { volume ->
                volume.chapters.map { it.id }.forEach { chapterId ->
                    localBookDataSource.updateChapterContent(
                        webBookDataSource.getChapterContent(
                            chapterId = chapterId,
                            bookId = bookId
                        ) ?: return Result.failure()
                    )
                }
            }
        }

        return Result.success()
    }
}