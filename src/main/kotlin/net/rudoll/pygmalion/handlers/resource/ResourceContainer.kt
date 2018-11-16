package net.rudoll.pygmalion.handlers.resource

import com.google.gson.Gson
import spark.Response
import java.util.*

class ResourceContainer(private val keyProperty: String) {

    private val resources = mutableMapOf<String, String>()
    private val gson = Gson()

    fun new(body: String, response: Response): String {
        val uuid = UUID.randomUUID().toString()
        resources[uuid] = body
        response.status(201)
        return uuid
    }

    fun getAll(): String {
        val allResources = resources.values.toList()
        return gson.toJson(allResources)
    }

    fun get(id: String, response: Response): String {
        val resource = resources[id]
        if (resource == null) {
            response.status(404)
            return ""
        }
        return resource
    }

    fun set(id: String, body: String, response: Response): String {
        resources[id] = body
        response.status(200)
        return ""
    }

    fun delete(id: String, response: Response): String {
        if (resources[id] == null) {
            response.status(404)
            return "Resource $id not found"
        }
        resources.remove(id)
        response.status(200)
        return ""
    }
}