package net.rudoll.pygmalion.handlers.resource

import com.google.gson.Gson
import com.google.gson.JsonParser
import spark.Response
import java.math.BigInteger
import java.util.UUID


class ResourceContainer(private val keyProperty: String) {

    private val resources = mutableMapOf<String, String>()
    private val gson = Gson()
    private val jsonParser = JsonParser()

    fun new(body: String, response: Response): String {
        val uuid = getNumericUUID()
        val newBody = setIdIfPossible(body, uuid)
        resources[uuid] = newBody
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
        val newBody = setIdIfPossible(body, id)
        resources[id] = newBody
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

    private fun setIdIfPossible(body: String, uuid: String):String{
        try {
            val bodyObject = jsonParser.parse(body).asJsonObject
            bodyObject.addProperty(keyProperty, uuid)
            return bodyObject.toString()
        } catch (e: Exception) {
            //this is an optional feature
        }
        return body
    }

    private fun getNumericUUID(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return String.format("%040d", BigInteger(uuid, 16))
    }
}