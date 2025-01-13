package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.utils.ImageDownloader
import io.nightfish.potatoepub.builder.EpubBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.io.File
import java.time.LocalDateTime

@HiltWorker
class ExportBookToEPUBWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val webBookDataSource: WebBookDataSource
) : Worker(appContext, workerParams) {
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    override fun doWork(): Result {
        val bookId = inputData.getInt("bookId", -1)
        val fileUri = inputData.getString("uri")?.let(Uri::parse) ?: return Result.failure()
        val tempDir = applicationContext.cacheDir.resolve("epub").resolve(bookId.toString())
        val cover = tempDir.resolve("cover.jpg")
        if (bookId < 0) return Result.failure()
        val tasks = mutableListOf<ImageDownloader.Task>()
        val epub = EpubBuilder().apply {
            val bookInformation = webBookDataSource.getBookInformation(bookId) ?: return Result.failure()
            val bookVolumes = webBookDataSource.getBookVolumes(bookId) ?: return Result.failure()
            val bookContentMap = mutableMapOf<Int, ChapterContent>()
            bookVolumes.volumes.forEach { volume ->
                volume.chapters.forEach {
                    bookContentMap[it.id] = webBookDataSource.getChapterContent(it.id, bookId) ?: return Result.failure()
                }
            }
            title = bookInformation.title
            modifier = LocalDateTime.now()
            creator = bookInformation.author
            description = bookInformation.description
            publisher = bookInformation.publishingHouse
            tasks.add(ImageDownloader.Task(cover, bookInformation.coverUrl))
            if (!tempDir.exists())
                tempDir.mkdirs()
            else
                tempDir.listFiles()?.forEach(File::delete)
            cover(cover)
            bookVolumes.volumes.forEach { volume ->
                chapter {
                    title(volume.volumeTitle)
                    volume.chapters.forEach {
                        chapter {
                            title(it.title)
                            content {
                                bookContentMap[it.id]!!.content.split("[image]").filter { it.isNotEmpty() }.forEach { singleText ->
                                    if (singleText.startsWith("http://") || singleText.startsWith("https://")) {
                                        val image = tempDir.resolve(singleText.hashCode().toString() + ".jpg")
                                        tasks.add(ImageDownloader.Task(image, singleText))
                                        image(image)
                                    }
                                    else {
                                        singleText.split("\n").forEach {
                                            text(it)
                                            br()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val imageDownloader = ImageDownloader(
            tasks = tasks,
            coroutineScope = coroutineScope,
            onFinished = {
                val file = tempDir.resolve("epub")
                epub.build().save(file)
                applicationContext.contentResolver.openOutputStream(fileUri).use {
                    it?.write(file.readBytes())
                }
                tempDir.delete()
            }
        )
        while (!imageDownloader.isDone) { //
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        coroutineScope.cancel()
    }
}