package net.rudoll.pygmalion.properties

import java.util.*

object PropertiesProvider {

    private var properties: Properties = Properties()

    private const val DEFAULT_SERVER_PORT = 4756

    init {
        properties.load(PropertiesProvider::class.java.getResourceAsStream("/webmock.properties"))
    }

    fun getVersion(): String {
        return try {
            properties.getProperty("version")
        } catch (e: Exception) {
            "N/A"
        }
    }

    fun getServerPort(): Int {
        return try {
            properties.getProperty("server.port").toInt()
        } catch (e: Exception) {
            DEFAULT_SERVER_PORT
        }
    }
}