package indi.dmzz_yyhyy.lightnovelreader.ui.components.calendar.core

fun <T : Comparable<T>> checkRange(start: T, end: T) {
    check(end >= start) {
        "start: $start is greater than end: $end"
    }
}
