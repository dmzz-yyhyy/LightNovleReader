package indi.dmzz_yyhyy.lightnovelreader.utils

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("NewApi")
fun formTime(time: LocalDateTime): String {
    val now = LocalDateTime.now()
    val yearDiff = now.year - time.year
    val monthDiff = now.monthValue - time.monthValue
    val dayDiff = now.dayOfYear - time.dayOfYear
    val hourDiff = now.hour - time.hour
    val minuteDiff = now.minute - time.minute

    return when {
        time == LocalDateTime.MIN -> "从未"
        yearDiff > 1 ->
            if (Locale.getDefault().language.equals(Locale.CHINESE.language))
                DateTimeFormatter
                    .ofPattern("uuuu年MMMd日", Locale.CHINESE)
                    .format(time)
            else
                DateTimeFormatter
                    .ofPattern("d MMM uuuu", Locale.ENGLISH)
                    .format(time)
        yearDiff == 1 ->  "去年"
        (dayDiff > 3 || monthDiff > 1) ->
            if (Locale.getDefault().language.equals(Locale.CHINESE.language))
                DateTimeFormatter
                    .ofPattern("MMMd日", Locale.CHINESE)
                    .format(time)
            else
                DateTimeFormatter
                    .ofPattern("d MMM", Locale.ENGLISH)
                    .format(time)
        dayDiff in 1..3 -> {
            val prefix = when (dayDiff) {
                1 -> "昨天"
                2 -> "前天"
                3 -> "大前天"
                else -> ""
            }
            if (dayDiff <=2) {
                "$prefix ${time.hour}:${time.minute}"
            } else {
                prefix
            }
        }
        hourDiff in 1..24 -> "$hourDiff 小时前"
        minuteDiff in 1 until 60 -> "$minuteDiff 分钟前"
        minuteDiff == 0 -> "刚刚"
        else -> "很久以前"
    }
}