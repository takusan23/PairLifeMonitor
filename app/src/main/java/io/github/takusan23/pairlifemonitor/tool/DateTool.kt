package io.github.takusan23.pairlifemonitor.tool

import java.text.SimpleDateFormat
import java.util.*

object DateTool {

    private val fullDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    private val dateOnlyDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    /**
     * UnixTime -> yyyy/MM/dd HH:mm:ss
     */
    fun formatFromUnixTimeMs(unixTimeMs: Long): String {
        return fullDateFormat.format(unixTimeMs)
    }

    /**
     * UnixTime -> yyyy/MM/dd
     */
    fun formatDateFromUnixTimeMs(unixTimeMs: Long): String {
        return dateOnlyDateFormat.format(unixTimeMs)
    }

}