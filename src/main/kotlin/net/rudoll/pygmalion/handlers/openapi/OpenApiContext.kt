package net.rudoll.pygmalion.handlers.openapi

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponses
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.util.HttpCallMapperUtil
import spark.Request
import spark.Response
import java.lang.IllegalStateException

class OpenApiContext(private val openAPI: OpenAPI) {

    fun apply(parsedInput: ParsedInput) {
        val paths = openAPI.paths
        paths.forEach { path -> this.applyPath(path, parsedInput) }
    }

    private fun applyPath(path: Map.Entry<String, PathItem>, parsedInput: ParsedInput) {
        applyOperation(path.key, "get", path.value.get, parsedInput)
        applyOperation(path.key, "post", path.value.post, parsedInput)
        applyOperation(path.key, "put", path.value.put, parsedInput)
        applyOperation(path.key, "options", path.value.options, parsedInput)
        applyOperation(path.key, "delete", path.value.delete, parsedInput)
    }

    private fun applyOperation(path: String, method: String, operation: Operation?, parsedInput: ParsedInput) {
        if (operation != null) {
            val resultCallback = getResultCallback(operation)
            parsedInput.actions.add(object : Action {
                override fun run(arguments: Set<ParsedArgument>) {
                    val pathWithArguments = getPathWithArguments(path)
                    parsedInput.logs.add("Mapping OpenAPI route $pathWithArguments with method $method")
                    HttpCallMapperUtil.map(method, pathWithArguments, parsedInput, resultCallback)
                }
            })
        }
    }

    private fun getPathWithArguments(path: String): String {
        var result = path.replace("}", "")
        result = result.replace("{", ":")
        return result
    }

    private fun getResultCallback(operation: Operation): HttpCallMapperUtil.ResultCallback {
        return object : HttpCallMapperUtil.ResultCallback {
            override fun getResult(request: Request, response: Response): String {
                return try {
                    val parameterValidationResult = validateParameters(request, operation)
                    if (!parameterValidationResult.isOk) {
                        response.status(400)
                        return parameterValidationResult.error
                    } else respond(response, operation.responses)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }
        }
    }


    private fun validateParameters(request: Request, operation: Operation): ValidationResponse {
        for (parameter in operation.parameters) {
            if (!parameter.required) {
                continue
            }
            if (parameter.`in` == "query") {
                val parameterValidationResult = validateQueryParameter(request, parameter)
                if (!parameterValidationResult.isOk) {
                    return parameterValidationResult
                }
            }
        }
        return ValidationResponse(true, "")
    }

    private fun validateQueryParameter(request: Request, parameter: Parameter): ValidationResponse {
        if (!request.queryParams().contains(parameter.name)) {
            return ValidationResponse(false, "Query parameter missing: ${parameter.name}")
        }
        return ValidationResponse(true, "")
    }

    private fun respond(response: Response, responses: ApiResponses): String {
        val firstMappedStatusCode = responses.keys.first()
        val apiResponse = responses[firstMappedStatusCode]!!
        response.status(firstMappedStatusCode.toInt())
        if (apiResponse.content == null) {
            return ""
        }
        val content = apiResponse.content
        val firstContentType = content.keys.first()
        response.header("Content-Type", firstContentType)
        val mediaType = content[firstContentType]!!
        if (mediaType.example != null) {
            return mediaType.example.toString()
        }
        val jsonObject = getFromSchema(mediaType.schema)
        return jsonObject.toString()
    }

    private fun getFromSchema(schema: Schema<*>): JsonElement {
        return when {
            schema.properties != null -> {
                val jsonObject = JsonObject()
                schema.properties.forEach { property -> jsonObject.add(property.key, getFromSchema(property.value)) }
                jsonObject
            }
            schema is ComposedSchema -> getFromComposedSchema(schema)
            schema.`$ref` != null -> getFromSchema(getSchemaByRef(schema.`$ref`))
            schema.type == "array" -> {
                val jsonArray = JsonArray()
                jsonArray.add(getFromSchema(getSchemaByRef((schema as ArraySchema).items.`$ref`)))
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
            schema.type == "string" -> JsonPrimitive("string")
            schema.type == "integer" -> JsonPrimitive(0)
            schema.type == "number" -> JsonPrimitive(0.0)
            schema.type == "boolean" -> JsonPrimitive(false)
            else -> throw IllegalStateException("Schema could not be parsed: $schema")
        }
    }

    private fun getSchemaByRef(ref: String): Schema<*> {
        val components = openAPI.components
        val refWithoutPrefix = ref.removePrefix("#/components/schemas/")
        return components.schemas[refWithoutPrefix]!!
    }
}