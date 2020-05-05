package net.rudoll.pygmalion.handlers.resource.persistence

import com.google.gson.JsonElement

interface ResourcePersistence {
    val values: List<JsonElement>

    operator fun get(key: String): JsonElement?
    operator fun set(key: String, value: JsonElement)
    fun remove(id: String)
}
