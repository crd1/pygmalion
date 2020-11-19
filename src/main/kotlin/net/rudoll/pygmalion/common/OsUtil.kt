package net.rudoll.pygmalion.common

object OsUtil {
    fun isLinux(): Boolean = System.getProperty("os.name").toLowerCase().contains("linux")
}
