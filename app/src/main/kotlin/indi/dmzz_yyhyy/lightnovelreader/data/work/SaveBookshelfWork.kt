package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@HiltWorker
class SaveBookshelfWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookshelfRepository: BookshelfRepository,
) : Worker(appContext, workerParams) {
    @Suppress("DuplicatedCode")
    override fun doWork(): Result {
        val bookshelfId = inputData.getInt("bookshelfId", -1)
        val fileUri = inputData.getString("uri")?.let(Uri::parse) ?: return Result.failure()
        val json =
            if (bookshelfId != -1)
                bookshelfRepository.exportBookshelfToJson(bookshelfId)
            else
                bookshelfRepository.exportAllBookshelvesJson()
        try {
            applicationContext.contentResolver.openFileDescriptor(fileUri, "w")?.use { parcelFileDescriptor ->
                ZipOutputStream(FileOutputStream(parcelFileDescriptor.fileDescriptor)).use {
                    it.putNextEntry(ZipEntry("data.json"))
                    it.write(json.toByteArray())
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