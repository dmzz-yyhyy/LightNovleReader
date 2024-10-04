package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

@HiltWorker
class ImportDataWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookshelfRepository: BookshelfRepository,
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val fileUri = inputData.getString("uri")?.let(Uri::parse) ?: return Result.failure()
        try {
            applicationContext.contentResolver.openFileDescriptor(fileUri, "r")?.use { parcelFileDescriptor ->
                FileInputStream(parcelFileDescriptor.fileDescriptor).use { fileInputStream ->
                    if (!bookshelfRepository.importBookshelfFromJsonData(
                            fileInputStream.bufferedReader().use {
                                it.readText()
                            }
                    )) {
                        return Result.failure()
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return Result.failure()
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.failure()
        }
        return Result.success()
    }
}