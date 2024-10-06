package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonSyntaxException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.data.BookRepository
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.json.AppUserDataContent
import indi.dmzz_yyhyy.lightnovelreader.data.json.AppUserDataJson
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

@HiltWorker
class ImportDataWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val webBookDataSource: WebBookDataSource,
    private val bookshelfRepository: BookshelfRepository,
    private val bookRepository: BookRepository,
    private val userDataRepository: UserDataRepository
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val fileUri = inputData.getString("uri")?.let(Uri::parse) ?: return Result.failure()
        var jsonText: String? = null
        var data: AppUserDataContent? = null
        try {
            applicationContext.contentResolver.openFileDescriptor(fileUri, "r")?.use { parcelFileDescriptor ->
                jsonText = FileInputStream(parcelFileDescriptor.fileDescriptor).use { fileInputStream ->
                    fileInputStream.bufferedReader().use {
                        it.readText()
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
        if (jsonText == null) return Result.failure()
        try {
            val appUserDataJson = AppUserDataJson.fromJson(jsonText!!)
            if (appUserDataJson.type == "light novel reader data file")
                data =
                    appUserDataJson.data.firstOrNull { it.webDataSourceId == webBookDataSource.id }
                        ?: return Result.failure()

        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            return Result.failure()
        }
        if (data == null) return Result.failure()
        bookshelfRepository.importBookshelf(data)
        bookRepository.importUserReadingData(data)
        userDataRepository.importUserData(data)
        return Result.success()
    }
}