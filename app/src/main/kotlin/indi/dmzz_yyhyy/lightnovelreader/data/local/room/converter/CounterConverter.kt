package indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter

import androidx.room.TypeConverter
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.Count

object CounterConverter {

    @TypeConverter
    fun fromCount(count: Count?): ByteArray? {
        return count?.toBinary()
    }

    @TypeConverter
    fun toCount(bytes: ByteArray?): Count? {
        return bytes?.let { Count.fromBinary(it) }
    }
}
