package indi.dmzz_yyhyy.lightnovelreader.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ImageDownloader(
    private val tasks: List<Task>,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    onFinished: () -> Unit,
) {
    private var count = 0
    val isDone get() = count == tasks.size
    data class Task(val file: File, val url: String)

    init {
        Log.i("ImageDownloader", "total tasks: ${tasks.size}")
        tasks.forEach {task ->
            coroutineScope.launch {
                getImageFromNetByUrl(task.url)?.let { writeImageToDisk(it, task.file) }
                count++
                Log.i("ImageDownloader", "tasks: ${count}/${tasks.size}")
                if (count == tasks.size) {
                    onFinished.invoke()
                }
            }
        }
    }

    private fun writeImageToDisk(data: ByteArray, file: File) {
        try {
            val fileParent = file.parentFile
            if (fileParent != null) {
                if (!fileParent.exists()) {
                    fileParent.mkdirs()
                    file.createNewFile()
                }
            }
            val fops = FileOutputStream(file)
            fops.write(data)
            fops.flush()
            fops.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getImageFromNetByUrl(strUrl: String): ByteArray? {
        try {
            val url = URL(strUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5 * 1000
            val inStream = conn.inputStream
            val btData = readInputStream(inStream)
            return btData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun readInputStream(inStream: InputStream): ByteArray {
        val outStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while ((inStream.read(buffer).also { len = it }) != -1) {
            outStream.write(buffer, 0, len)
        }
        inStream.close()
        return outStream.toByteArray()
    }
}
