package net.rudoll.pygmalion.handlers.resource

import com.google.gson.*
import net.rudoll.pygmalion.model.ParsedInput
import spark.Response
import java.io.File
import java.math.BigInteger
import java.util.*


class ResourceContainer(private val keyProperty: String, val name: String) {

    internal val resources = mutableMapOf<String, JsonElement>()
    private val gson = Gson()
    private val jsonParser = JsonParser()

    fun init(initialRepoFile: String, parsedInput: ParsedInput): Boolean {
        return try {
            parsedInput.logs.add("Using initial repository from file: $initialRepoFile")
            val repoFileText = File(initialRepoFile).readText()
            val repoFileParsed = jsonParser.parse(repoFileText).asJsonArray
            storeInitialResources(initialValues = repoFileParsed, parsedInput = parsedInput)
        } catch (e: Exception) {
            parsedInput.errors.add("Initial repository could not be parsed from file.")
            false
        }
    }

    private fun storeInitialResources(initialValues: JsonArray, parsedInput: ParsedInput): Boolean {
        for (i in 0 until initialValues.size()) {
            try {
                val objectId = getId(initialValues[i].asJsonObject)
                resources[objectId] = initialValues[i].asJsonObject
            } catch (e: Exception) {
                parsedInput.errors.add("Not all elements in the initial repository have a predefined id.")
                return false
            }
        }
        return true
    }

    fun new(body: String, response: Response): String {
        val id = try {
            val jsonBody = jsonParser.parse(body)
            getId(jsonBody.asJsonObject)
        } catch (e: Exception) {
            //has no id inside. does not matter, we'll generate one
            getNumericUUID()
        }
        val newBody = setIdIfPossible(body, id)
        resources[id] = newBody
        response.status(201)
        return newBody.toString()
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
        return resource.toString()
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

    private fun getId(jsonObject: JsonObject): String {
        if (!jsonObject.has(keyProperty)) {
            throw IllegalArgumentException("No key property on object")
        }
        val jsonId = jsonObject.get(keyProperty).asJsonPrimitive.toString()
        return jsonId.replace("\"", "")
    }

    private fun setIdIfPossible(body: String, uuid: String): JsonElement {
        try {
            val bodyObject = jsonParser.parse(body).asJsonObject
            bodyObject.addProperty(keyProperty, uuid)
            return bodyObject
        } catch (e: Exception) {
            //this is an optional feature
        }
        return JsonPrimitive(body)
    }

    private fun getNumericUUID(): String {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        return String.format("%040d", BigInteger(uuid, 16))
    }

    companion object {
        const val DEFAULT_KEY_PROPERTY = "id"
    }
}