package net.rudoll.pygmalion.handlers.openapi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import java.lang.IllegalStateException
import java.util.*

class ExampleResponseGenerator(private val openAPI: OpenAPI) {

    fun getFromSchema(schema: Schema<*>): JsonElement {
        return when {
            schema.properties != null -> {
                val jsonObject = JsonObject()
                schema.properties.forEach { property -> jsonObject.add(property.key, getFromSchema(property.value)) }
                jsonObject
            }
            schema is ComposedSchema -> getFromComposedSchema(schema)
            schema.`$ref` != null -> getFromSchema(OpenApiContext.getSchemaByRef(schema.`$ref`, openAPI))
            schema.type == "array" -> {
                val jsonArray = JsonArray()
                jsonArray.add(getFromSchema((schema as ArraySchema).items))
                return jsonArray
            }
            else -> getPrimitive(schema)
        }
    }

    private fun getFromComposedSchema(composedSchema: ComposedSchema): JsonElement {
        val mergedSchemas = mutableSetOf<Schema<*>>()
        //frankly, I don't care
        composedSchema.allOf?.forEach { schema -> mergedSchemas.add(schema) }
        composedSchema.anyOf?.forEach { schema -> mergedSchemas.add(schema) }
        composedSchema.oneOf?.forEach { schema -> mergedSchemas.add(schema) }
        return getFromSchema(mergedSchemas.first())
    }

    private fun getPrimitive(schema: Schema<*>): JsonElement {
        return when {
            schema.type == "string" -> getStringPrimitive(schema.format)
            schema.type == "integer" -> JsonPrimitive(0)
            schema.type == "number" -> JsonPrimitive(0.0)
            schema.type == "boolean" -> JsonPrimitive(false)
            else -> throw IllegalStateException("Schema could not be parsed: $schema")
        }
    }

    private fun getStringPrimitive(format: String?): JsonElement {
        var value = "string"
        if (format != null && format.toLowerCase() == "uuid") {
            value = UUID.randomUUID().toString()
        }
        return JsonPrimitive(value)
    }
}
