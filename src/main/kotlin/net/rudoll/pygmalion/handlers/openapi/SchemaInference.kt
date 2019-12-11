package net.rudoll.pygmalion.handlers.openapi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema

object SchemaInference {
    fun from(elements: Set<JsonElement>): Schema<Any> {
        val inferredSchemata = mutableListOf<Schema<Any>>()
        for (element in elements) {
            inferredSchemata.add(inferSchema(element))
        }
        return if (!inferredSchemata.isEmpty()) inferredSchemata[0] else Schema() //we currently always use the first encountered element
    }

    private fun inferSchema(element: JsonElement): Schema<Any> {
        if (element is JsonPrimitive) {
            return element.toSchema()
        }

        if (element is JsonObject) {
            val schema = Schema<Any>()
            schema.type = "object"
            schema.properties = mutableMapOf()
            element.keySet().forEach { schema.properties[it] = inferSchema(element[it]) }
            return schema
        } else if (element is JsonArray) {
            val arraySchema = ArraySchema()
            arraySchema.type = "array"
            arraySchema.items = from(getJsonElements(element))
            return arraySchema
        }
        return Schema()
    }

    private fun getJsonElements(element: JsonArray): Set<JsonElement> {
        val set = mutableSetOf<JsonElement>()
        val iterator = element.iterator()
        while (iterator.hasNext()) {
            set.add(iterator.next())
        }
        return set.toSet()
    }
}

private fun JsonPrimitive.toSchema(): Schema<Any> {
    val schema = Schema<Any>()
    when {
        this.isBoolean -> schema.type = "boolean"
        this.isNumber -> schema.type = "number"
        this.isString -> schema.type = "string"
    }
    return schema
}
