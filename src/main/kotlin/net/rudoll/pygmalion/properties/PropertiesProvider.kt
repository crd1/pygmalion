package net.rudoll.pygmalion.properties

import java.util.*

object PropertiesProvider {

    private var properties: Properties = Properties()

    init {
        properties.load(PropertiesProvider::class.java.getResourceAsStream("/pygmalion.properties"))
    }

    fun getVersion(): String {
        return try {
            properties.getProperty("version")
        } catch (e: Exception) {
            "N/A"
        }
    }
}