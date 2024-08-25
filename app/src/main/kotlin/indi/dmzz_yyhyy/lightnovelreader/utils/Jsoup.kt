package indi.dmzz_yyhyy.lightnovelreader.utils

import android.util.Log
import java.io.IOException
import java.lang.Thread.sleep
import java.net.SocketException
import javax.net.ssl.SSLHandshakeException
import org.jsoup.Connection
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Document

fun Connection.autoReconnectionGet(lastReconnectTimes: Int = 10, lastReconnectTime: Int = 250): Document? =
    try {
        this.get()
    } catch (e: HttpStatusException) {
        Log.e("Network", "failed to get data from ${e.url}")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionGet(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: SocketException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionGet(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: SSLHandshakeException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionGet(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: IOException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionGet(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    }

fun Connection.autoReconnectionPost(lastReconnectTimes: Int = 10, lastReconnectTime: Int = 250): Document? =
    try {
        this.post()
    } catch (e: HttpStatusException) {
        Log.e("Network", "failed to get data from ${e.url}")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionPost(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: SocketException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionPost(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: SSLHandshakeException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionPost(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    } catch (e: IOException) {
        Log.e("Network", "failed to get data")
        e.printStackTrace()
        if (lastReconnectTime > 1) {
            sleep(lastReconnectTime.toLong())
            this.autoReconnectionPost(lastReconnectTimes - 1, lastReconnectTime * 2)
        }
        else
            null
    }