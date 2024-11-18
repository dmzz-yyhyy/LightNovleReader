package indi.dmzz_yyhyy.lightnovelreader.utils

import android.graphics.BitmapFactory
import android.util.Size
import java.net.HttpURLConnection
import java.net.URL


fun getImageSize(imageUrl: String?): Size? {
    var connection: HttpURLConnection? = null
    try {
        val requestUrl = URL(imageUrl)
        connection = requestUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 8000
        connection.readTimeout = 8000
        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val inputStream = connection.inputStream
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            return Size(options.outWidth, options.outHeight)
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        connection?.disconnect()
    }
    return null
}