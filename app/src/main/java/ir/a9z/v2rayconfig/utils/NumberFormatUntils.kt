package ir.a9z.v2rayconfig.utils

fun EnToFa(input: String): String {
    val persianNumbers = listOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    val englishNumbers = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    var result = input
    for (i in englishNumbers.indices) {
        result = result.replace(englishNumbers[i], persianNumbers[i])
    }
    return result
}

fun EnToFa(input: Int): String {
    return EnToFa(input.toString())
}