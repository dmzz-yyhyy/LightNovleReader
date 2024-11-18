package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics

import java.time.LocalDate
import kotlin.experimental.and
import kotlin.experimental.or

class Count {
    private val bits = BooleanArray(144)

    fun setMinute(hour: Int, minuteCount: Int) {
        require(hour in 0..23) { "Hour value must be between [0, 24], but got $hour." }
        require(minuteCount in 0..60) { "Minute count must be between [0, 60], but got $minuteCount." }

        val limitedMinuteCount = minuteCount and 0b111111

        val bitIndex = hour * 6
        for (i in 0 until 6) {
            bits[bitIndex + i] = (limitedMinuteCount shr i) and 1 == 1
        }
    }

    fun getMinute(hour: Int): Int {
        val bitIndex = hour * 6
        var result = 0

        for (i in 5 downTo 0) {
            result = (result shl 1) or if (bits[bitIndex + i]) 1 else 0
        }

        return result
    }

    fun getHourStatistics(): Map<Int, Int> {
        return (0 until 24).associateWith { getMinute(it) }
    }

    fun toBinary(): ByteArray {
        val result = ByteArray(18)

        for (hour in 0 until 24) {
            val minute = getMinute(hour)
            val bitIndex = hour * 6

            for (i in 0 until 6) {
                val byteIndex = (bitIndex + i) / 8
                val bitOffset = (bitIndex + i) % 8
                val bit = (minute shr (5 - i)) and 1

                if (bit == 1) {
                    result[byteIndex] = result[byteIndex] or (1 shl (7 - bitOffset)).toByte()
                } else {
                    result[byteIndex] = result[byteIndex] and (1 shl (7 - bitOffset)).inv().toByte()
                }
            }
        }
        return result
    }

    fun getTotalMinutes(): Int {
        return (0 until 24).sumOf { getMinute(it) }
    }

    companion object {
        fun fromBinary(data: ByteArray): Count {
            require(data.size == 18) { "Data size must be 18 bytes, but got ${data.size}" }
            val count = Count()

            for (hour in 0 until 24) {
                val bitIndex = hour * 6
                var bitValue = 0

                for (i in 0 until 6) {
                    val byteIndex = (bitIndex + i) / 8
                    val bitOffset = (bitIndex + i) % 8
                    val bit = (data[byteIndex].toInt() shr (7 - bitOffset)) and 1

                    bitValue = (bitValue shl 1) or bit
                }

                count.setMinute(hour, bitValue)
            }

            return count
        }
    }
}

class DateTimeCounter {
    private val dateCounters = mutableMapOf<LocalDate, Count>()

    fun setMinute(date: LocalDate, hour: Int, min: Int) {
        require(min in 0..60) { "Minute count must be between 0 and 60, but got $min." }
        val dailyCount = dateCounters.getOrPut(date) { Count() }
        dailyCount.setMinute(hour, min)
    }

    fun getMinute(date: LocalDate, hour: Int): Int {
        val dailyCounter = dateCounters[date]
        return dailyCounter?.getMinute(hour) ?: 0
    }

    fun getDateStatistics(date: LocalDate): Map<Int, Int> {
        return dateCounters[date]?.getHourStatistics() ?: emptyMap()
    }

    fun getTotalMinutesForDay(date: LocalDate): Int {
        return dateCounters[date]?.getTotalMinutes() ?: 0
    }

    fun toBinary(): ByteArray {
        val result = mutableListOf<Byte>()
        for (counter in dateCounters.values) {
            result.addAll(counter.toBinary().toList())
        }
        return result.toByteArray()
    }
}

fun main() {

    val count = Count()
    count.setMinute(0, 60)
    count.setMinute(12, 30)
    count.setMinute(23, 15)

    val dateTimeCounter = DateTimeCounter()
    val date1 = LocalDate.of(2024, 11, 1)

    dateTimeCounter.apply {
        setMinute(date1, 0, 60)
        setMinute(date1, 1, 60)
        setMinute(date1, 3, 60)
        setMinute(date1, 4, 60)
        setMinute(date1, 5, 60)
        setMinute(date1, 6, 60)
        setMinute(date1, 23, 60)
    }

    val binaryOutput = dateTimeCounter.toBinary()
    println("Binary Output: ${binaryOutput.joinToString(" ") { it.toUByte().toString(2).padStart(8, '0') }}")

    val hourStatisticsDate1 = dateTimeCounter.getDateStatistics(date1)
    println("Hour statistics for $date1:\n$hourStatisticsDate1")

    val totalMinutesDate1 = dateTimeCounter.getTotalMinutesForDay(date1)
    println("Total minutes for $date1: $totalMinutesDate1")
}
