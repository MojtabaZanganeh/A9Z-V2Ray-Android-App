package ir.a9z.v2rayconfig.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun gregorianToJalali(gy: Int, gm: Int, gd: Int): IntArray {
    var g_d_m: IntArray = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    var gy2: Int = if (gm > 2) (gy + 1) else gy
    var days: Int = 355666 + (365 * gy) + ((gy2 + 3) / 4).toInt() - ((gy2 + 99) / 100).toInt() + ((gy2 + 399) / 400).toInt() + gd + g_d_m[gm - 1]
    var jy: Int = -1595 + (33 * (days / 12053).toInt())
    days %= 12053
    jy += 4 * (days / 1461).toInt()
    days %= 1461
    if (days > 365) {
        jy += ((days - 1) / 365).toInt()
        days = (days - 1) % 365
    }
    var jm: Int; var jd: Int;
    if (days < 186) {
        jm = 1 + (days / 31).toInt()
        jd = 1 + (days % 31)
    } else {
        jm = 7 + ((days - 186) / 30).toInt()
        jd = 1 + ((days - 186) % 30)
    }
    return intArrayOf(jy, jm, jd)
}

fun formatPersianDate(timestamp: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
        val date = inputFormat.parse(timestamp)

        if (date == null) {
            throw IllegalArgumentException("Invalid timestamp format")
        }

        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)

        val jalaliDate = gregorianToJalali(year, month, day)
        val jalaliYear = jalaliDate[0]
        val jalaliMonth = jalaliDate[1]
        val jalaliDay = jalaliDate[2]

        val persianMonths = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )

        val formattedDate = "$jalaliDay ${persianMonths[jalaliMonth - 1]} $jalaliYear"

        return formattedDate
    } catch (e: Exception) {
        println("Error formatting date: ${e.message}")
        return "Invalid Date"
    }
}
