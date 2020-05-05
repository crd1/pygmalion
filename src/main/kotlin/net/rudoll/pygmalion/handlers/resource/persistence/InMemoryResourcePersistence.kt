package net.rudoll.pygmalion.handlers.resource.persistence

import com.google.gson.JsonElement

class InMemoryResourcePersistence : ResourcePersistence {
    private val _resources = mutableMapOf<String, JsonElement>()
    override val values: List<JsonElement>
        get() = this._resources.values.toList()

    override fun get(key: String): JsonElement? {
        return this._resources[key]
    }

    override fun set(key: String, value: JsonElement) {
        this._resources[key] = value
    }

    override fun remove(id: String) {
        this._resources.remove(id)
    }
}
