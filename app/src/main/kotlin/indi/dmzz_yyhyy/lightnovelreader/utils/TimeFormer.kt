package indi.dmzz_yyhyy.lightnovelreader.utils

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SuppressLint("NewApi")
fun formTime(time: LocalDateTime): String {
    val now = LocalDateTime.now()
    val dayDiff = now.dayOfYear - time.dayOfYear
    val hourDiff = now.hour - time.hour
    val minuteDiff = now.minute - time.minute

    return when {
        time == LocalDateTime.MIN -> "从未"
        time.isAfter(now) -> {
            val formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm")
            time.format(formatter)
        }
        time.year < now.year -> "去年"
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
        minuteDiff == 0 -> "刚刚"
        minuteDiff in 1 until 60 -> "$minuteDiff 分钟前"
        else -> "很久之前"
    }
}