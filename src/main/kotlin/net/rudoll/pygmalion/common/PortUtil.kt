package net.rudoll.pygmalion.common

import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.StateHolder
import spark.Spark

object PortUtil {
    fun setPort(port: Int?): Boolean {
        if (port == null) {
            if (!StateHolder.state.portSet) {
                Spark.port(80)
                StateHolder.state.portSet = true
            }
            return true
        }
        if (StateHolder.state.portSet) {
            return false
        }
        Spark.port(port)
        StateHolder.state.portSet = true
        OpenApiMonitor.setPort(port)
        return true
    }

    fun getPortAndRoute(target: String): PortAndRoute {
        val regex = "^[0-9]{2,5}:*".toRegex()
        val port = regex.find(target)?.value?.replace(":", "")
        val route = target.substring(port?.length ?: 0)
        return PortAndRoute(port?.toInt(), route)
    }

    fun ensurePortIsSet(parsedInput: ParsedInput) {
        if (!StateHolder.state.portSet) {
            parsedInput.logs.add("Setting default port 80")
            setPort(80)
        }
    }

    data class PortAndRoute(val port: Int?, val route: String)
}