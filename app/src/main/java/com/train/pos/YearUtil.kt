package com.train.pos

object YearUtil {

    fun startOfYear(year: Int): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(year, 0, 1, 0, 0, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun endOfYear(year: Int): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(year, 11, 31, 23, 59, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
