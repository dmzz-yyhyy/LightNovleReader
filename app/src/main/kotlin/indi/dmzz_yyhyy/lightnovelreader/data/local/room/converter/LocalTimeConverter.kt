package indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter

import androidx.room.TypeConverter
import java.time.LocalTime

object LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?): Int? {
        return localTime?.let { it.hour * 60 + it.minute }
    }

    @TypeConverter
    fun toLocalTime(minutes: Int?): LocalTime? {
        return minutes?.let { LocalTime.of(it / 60, it % 60) }
    }
}
