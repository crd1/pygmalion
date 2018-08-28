package net.rudoll.pygmalion.util

import net.rudoll.pygmalion.handlers.port.PortHandler
import spark.Spark

object PortUtil {
    fun setPort(port: Int?): Boolean {
        if (port == null) {
            if (!PortHandler.portSet) {
                Spark.port(80)
                PortHandler.portSet = true
            }
            return true
        }
        if (PortHandler.portSet) {
            return false
        }
        Spark.port(port)
        PortHandler.portSet = true
        return true
    }

    fun getPortAndRoute(target: String): PortAndRoute {
        val regex = "^[0-9]{2,5}:*".toRegex()
        val port = regex.find(target)?.value?.replace(":", "")
        val route = target.substring(port?.length ?: 0)
        return PortAndRoute(port?.toInt(), route)
    }

    data class PortAndRoute(val port: Int?, val route: String)
}