package indi.dmzz_yyhyy.lightnovelreader.data.local.room.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateConverter {
    private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    @TypeConverter
    fun localDateToInt(date: LocalDate?): Int? {
        return date?.format(formatter)?.toInt()
    }

    @TypeConverter
    fun intToLocalDate(dateInt: Int?): LocalDate? {
        return dateInt?.let {
            LocalDate.parse(it.toString(), formatter)
        }
    }
}
