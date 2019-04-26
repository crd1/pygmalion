package net.rudoll.pygmalion.handlers.openapi

import com.google.gson.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.RequestBody
import spark.Request

class RequestValidator(private val openAPI: OpenAPI) {

    private val jsonParser = JsonParser()

    fun validateRequest(request: Request, operation: Operation): ValidationResult {
        if (operation.parameters != null) {
            for (parameter in operation.parameters) {
                if (!parameter.required) {
                    continue
                }
                val parameterValidationResult = validateUrlParameter(request, parameter)
                if (!parameterValidationResult.isOk) {
                    return parameterValidationResult
                }
            }
        }
        if (operation.requestBody != null) {
            val bodyValidationResult = validateBody(request, operation.requestBody)
            if (!bodyValidationResult.isOk) {
                return bodyValidationResult
            }
        }
        return ValidationResult(true, "Request is OK")
    }

    private fun validateBody(request: Request, requestBody: RequestBody): ValidationResult {
        if (!requestBody.required) {
            return ValidationResult(true, "Body is not required")
        }
        val contentSpecification = requestBody.content[request.contentType()]
                ?: return ValidationResult(false, "Request body has wrong content type: ${request.contentType()}")
        val contentValidationResult = validateContent(request.body(), contentSpecification.schema, request.contentType())
        if (!contentValidationResult.isOk) {
            return contentValidationResult
        }
        return ValidationResult(true, "Body is OK")
    }

    private fun validateContent(body: String, contentSpecification: Schema<*>, contentType: String): ValidationResult {
        if (contentType == "text/plain") {
            return ValidationResult(true, "text/plain does not need to be validated.")
        }
        // we only support json bodies from here on.
        val parsedBody = jsonParser.parse(body)
        val bodyValidationResult = validateJsonAgainstSchema(parsedBody, contentSpecification)
        if (!bodyValidationResult.isOk) {
            return bodyValidationResult
        }
        return ValidationResult(true, "Content is OK")
    }

    private fun validateJsonAgainstSchema(jsonElement: JsonElement, schema: Schema<*>): ValidationResult {
        return when {
            schema.properties != null && !schema.properties.isEmpty() -> {
                if (jsonElement !is JsonObject) {
                    return ValidationResult(false, "Expected $jsonElement in schema ${schema.name} to be an object.")
                }
                var propertyValidationResult: ValidationResult
                for (property in schema.properties) {
                    val propertyInJson = jsonElement.asJsonObject.get(property.key)
                    if (propertyInJson == null) {
                        if (schema.required.contains(property.key)) {
                            return ValidationResult(false, "Missing required property ${property.key} in body.")
                        }
                        continue
                    }
                    propertyValidationResult = validateJsonAgainstSchema(propertyInJson, property.value)
                    if (!propertyValidationResult.isOk) {
                        return propertyValidationResult
                    }
                }
                ValidationResult(true, "Properties are OK")
            }
            schema is ComposedSchema -> validateJsonAgainstComposedSchema(jsonElement, schema)
            schema.`$ref` != null -> validateJsonAgainstSchema(jsonElement, OpenApiContext.getSchemaByRef(schema.`$ref`, openAPI))
            schema.type == "array" -> {
                if (jsonElement !is JsonArray) {
                    return ValidationResult(false, "Expected body property ${schema.name} to be an array.")
                }
                var childValidationResult: ValidationResult
                for (jsonChild in jsonElement) {
                    childValidationResult = validateJsonAgainstSchema(jsonChild, OpenApiContext.getSchemaByRef((schema as ArraySchema).items.`$ref`, openAPI))
                    if (!childValidationResult.isOk) {
                        return childValidationResult;
                    }
                }
                return ValidationResult(true, "Array is OK")
            }
            else -> validateJsonPrimitive(jsonElement, schema)
        }
    }

    private fun validateJsonPrimitive(jsonElement: JsonElement, schema: Schema<*>): ValidationResult {
        if (!jsonElement.toString().matchesOpenApiType(schema.type)) {
            return ValidationResult(false, "Primitive body property $jsonElement does not match type ${schema.type}")
        }
        return ValidationResult(true, "Primitive is OK")
    }

    private fun validateJsonAgainstComposedSchema(jsonElement: JsonElement, schema: ComposedSchema): ValidationResult {
        var composedSchemaValidationResult: ValidationResult
        if (schema.allOf != null) {
            composedSchemaValidationResult = validateJsonAgainstAllOf(jsonElement, schema.allOf)
            if (!composedSchemaValidationResult.isOk) {
                return composedSchemaValidationResult
            }
        }
        if (schema.oneOf != null) {
            composedSchemaValidationResult = validateJsonAgainstOneOf(jsonElement, schema.oneOf)
            if (!composedSchemaValidationResult.isOk) {
                return composedSchemaValidationResult
            }
        }
        if (schema.anyOf != null) {
            composedSchemaValidationResult = validateJsonAgainstAnyOf(jsonElement, schema.anyOf)
            if (!composedSchemaValidationResult.isOk) {
                return composedSchemaValidationResult
            }
        }
        return ValidationResult(true, "Composed Schema is OK")
    }

    private fun validateJsonAgainstAllOf(jsonElement: JsonElement, allOf: List<Schema<*>>): ValidationResult {
        for (schema in allOf) {
            val validationResult = validateJsonAgainstSchema(jsonElement, schema)
            if (!validationResult.isOk) {
                return validationResult
            }
        }
        return ValidationResult(true, "AllOf OK")
    }

    private fun validateJsonAgainstAnyOf(jsonElement: JsonElement, anyOf: List<Schema<*>>): ValidationResult {
        for (schema in anyOf) {
            if (validateJsonAgainstSchema(jsonElement, schema).isOk) {
                return ValidationResult(true, "AnyOf OK")
            }
        }
        return ValidationResult(false, "JsonElement $jsonElement did not match any allowed schema.")
    }

    private fun validateJsonAgainstOneOf(jsonElement: JsonElement, oneOf: List<Schema<*>>): ValidationResult {
        var matched = false
        for (schema in oneOf) {
            val validationResult = validateJsonAgainstSchema(jsonElement, schema)
            if (validationResult.isOk) {
                if (matched) {
                    return ValidationResult(false, "JsonElement $jsonElement matches multiple schemata.")
                }
                matched = true
            }
        }
        if (!matched) {
            return ValidationResult(false, "JsonElement $jsonElement did not match any allowed schema from oneOf.")
        }
        return ValidationResult(true, "OneOf OK")
    }

    private fun validateUrlParameter(request: Request, parameter: Parameter): ValidationResult {
        val parameterFromRequest = getParameterFromRequest(request, parameter)
                ?: return ValidationResult(false, "Parameter ${parameter.name} missing in ${parameter.`in`}.")
        if (!parameterFromRequest.matchesOpenApiType(parameter.schema.type)) {
            return ValidationResult(false, "Parameter ${parameter.name} in ${parameter.`in`} does not match type ${parameter.schema.type}")
        }
        return ValidationResult(true, "URL parameters are OK")
    }

    private fun getParameterFromRequest(request: Request, parameter: Parameter): String? {
        return when {
            parameter.`in` == "query" -> request.queryParams(parameter.name)
            parameter.`in` == "path" -> request.params(parameter.name)
            else -> throw IllegalArgumentException("Unrecognized parameter location: ${parameter.`in`}")
        }
    }
}

private fun String.matchesOpenApiType(type: String): Boolean {
    return when (type) {
        "string" -> true
        "integer" -> this.toIntOrNull() != null
        "number" -> this.toDoubleOrNull() != null
        "boolean" -> this.toBoolean() || this.equals("false", ignoreCase = true)
        else -> throw IllegalStateException("Type could not be parsed: $type")
    }
}