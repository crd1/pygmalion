package net.rudoll.pygmalion.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateFormatter {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")

    fun now(): String {
        return dateFormatter.format(LocalDateTime.now())
    }
}
