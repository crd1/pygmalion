package net.rudoll.pygmalion.handlers.openapi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema

object SchemaInference {
    fun from(elements: Set<JsonElement>): Schema<*> {
        val inferredSchemata = mutableListOf<Schema<*>>()
        for (element in elements) {
            inferredSchemata.add(inferSchema(element))
        }
        return mergeSchemata(inferredSchemata)
    }

    private fun mergeSchemata(inferredSchemata: List<Schema<*>>): Schema<*> {
        if (inferredSchemata.isEmpty()) {
            return Schema<Any>()
        }
        val firstObservedSchema = inferredSchemata[0]
        return if (firstObservedSchema is ArraySchema) {
            mergeArraySchemata(inferredSchemata)
        } else {
            mergeObjectSchemata(inferredSchemata)
        }
    }

    private fun mergeObjectSchemata(inferredSchemata: List<Schema<*>>): Schema<*> {
        if (inferredSchemata[0].type != "object") {
            return inferredSchemata[0] //not mergeable, this is our best guess
        }
        val mergedSchema = Schema<Any>()
        mergedSchema.properties = mutableMapOf()
        inferredSchemata.forEach { mergedSchema.properties.putAll(it.properties) }
        return mergedSchema
    }

    private fun mergeArraySchemata(inferredSchemata: List<Schema<*>>): ArraySchema {
        return ArraySchema().type("array").items(mergeObjectSchemata(inferredSchemata.filter { it is ArraySchema }.map { ((it as ArraySchema).items as Schema<*>) }))
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
