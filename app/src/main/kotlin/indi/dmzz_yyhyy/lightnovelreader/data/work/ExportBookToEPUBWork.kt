package indi.dmzz_yyhyy.lightnovelreader.data.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import indi.dmzz_yyhyy.lightnovelreader.R
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

    private val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var notification: Notification? = null
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "BookEpubExport",
                "EPUB 导出进度",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification() {
        notification = NotificationCompat.Builder(applicationContext, "BookEpubExport")
            .setContentTitle("导出 ${inputData.getString("title")}")
            .setContentText("处理中...")
            .setSmallIcon(R.drawable.file_export_24px)
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun updateFailureNotification() {
        notification = NotificationCompat.Builder(applicationContext, "BookEpubExport")
            .setContentTitle("导出 ${inputData.getString("title")}")
            .setContentText("导出失败")
            .setSmallIcon(R.drawable.file_export_24px)
            .setProgress(0, 0, false)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun updateCompletionNotification() {
        notification = NotificationCompat.Builder(applicationContext, "BookEpubExport")
            .setContentTitle("导出 ${inputData.getString("title")}")
            .setContentText("已完成")
            .setSmallIcon(R.drawable.file_export_24px)
            .setProgress(0, 0, false)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun doWork(): Result {
        createNotificationChannel()
        showProgressNotification()

        val bookId = inputData.getInt("bookId", -1)
        val fileUri = inputData.getString("uri")?.let(Uri::parse) ?: return Result.failure()
        val tempDir = applicationContext.cacheDir.resolve("epub").resolve(bookId.toString())
        val cover = tempDir.resolve("cover.jpg")
        if (bookId < 0) {
            updateFailureNotification()
            return Result.failure()
        }

        val tasks = mutableListOf<ImageDownloader.Task>()
        val epub = EpubBuilder().apply {
            val bookInformation = webBookDataSource.getBookInformation(bookId) ?: return Result.failure().also {
                updateFailureNotification()
            }
            val bookVolumes = webBookDataSource.getBookVolumes(bookId) ?: return Result.failure().also {
                updateFailureNotification()
            }
            val bookContentMap = mutableMapOf<Int, ChapterContent>()
            bookVolumes.volumes.forEach { volume ->
                volume.chapters.forEach {
                    bookContentMap[it.id] = webBookDataSource.getChapterContent(it.id, bookId) ?: return Result.failure().also {
                        updateFailureNotification()
                    }
                }
            }
            title = bookInformation.title
            modifier = LocalDateTime.now()
            creator = bookInformation.author
            description = bookInformation.description
            publisher = bookInformation.publishingHouse
            tasks.add(ImageDownloader.Task(cover, bookInformation.coverUrl))
            if (!tempDir.exists()) tempDir.mkdirs()
            else tempDir.listFiles()?.forEach(File::delete)
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
                                    } else {
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
                updateCompletionNotification()
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
