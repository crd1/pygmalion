package net.rudoll.pygmalion.handlers.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.common.PortManager
import net.rudoll.pygmalion.handlers.oauth.OAuthRouteMapper
import spark.Request
import spark.Response
import java.net.URL

class OpenApiContext(private val openAPI: OpenAPI) {

    private val PREFERRED_CONTENT_TYPE: String = "application/json"

    fun apply(parsedInput: ParsedInput) {
        val paths = openAPI.paths
        setPort(openAPI.servers, parsedInput)
        paths.forEach { path -> this.applyPath(path, parsedInput) }
        openAPI.components?.securitySchemes?.let {
            parsedInput.actions.add(object : Action {
                override fun run(arguments: Set<ParsedArgument>) {
                    applySecuritySchemes(it, parsedInput)
                }
            })
        }
    }

    private fun applySecuritySchemes(securitySchemes: Map<String, SecurityScheme>, parsedInput: ParsedInput) {
        securitySchemes.forEach { scheme ->
            scheme.value.flows?.authorizationCode?.let {
                OAuthRouteMapper.createOAuthRoutes(it.authorizationUrl, it.tokenUrl, parsedInput)
            }
        }
    }

    private fun setPort(servers: MutableList<Server>?, parsedInput: ParsedInput) {
        if (servers == null || servers.isEmpty()) {
            return
        }
        var portSet = false
        try {
            val port = URL(servers.first().url).port
            if (port != -1) {
                portSet = PortManager.setPort(port)
            }
        } catch (e: Exception) {
            //ignore
        }
        if (!portSet) {
            parsedInput.errors.add("Could not set port from OpenAPI specification.")
        }
    }

    private fun applyPath(path: Map.Entry<String, PathItem>, parsedInput: ParsedInput) {
        applyOperation(path.key, "get", path.value.get, parsedInput)
        applyOperation(path.key, "post", path.value.post, parsedInput)
        applyOperation(path.key, "put", path.value.put, parsedInput)
        applyOperation(path.key, "options", path.value.options, parsedInput)
        applyOperation(path.key, "delete", path.value.delete, parsedInput)
        applyOperation(path.key, "patch", path.value.patch, parsedInput)
    }

    private fun applyOperation(path: String, method: String, operation: Operation?, parsedInput: ParsedInput) {
        if (operation != null) {
            val resultCallback = getResultCallback(operation)
            parsedInput.actions.add(object : Action {
                override fun run(arguments: Set<ParsedArgument>) {
                    val pathWithArguments = getPathWithArguments(path)
                    parsedInput.logs.add("Mapping OpenAPI route $pathWithArguments with method $method")
                    HttpCallMapper.map(method, pathWithArguments, parsedInput, resultCallback)
                }
            })
        }
    }

    private fun getPathWithArguments(path: String): String {
        var result = path.replace("}", "")
        result = result.replace("{", ":")
        return result
    }

    private fun getResultCallback(operation: Operation): HttpCallMapper.ResultCallback {
        return object : HttpCallMapper.ResultCallback {
            override fun getResultCallbackDescription(): HttpCallMapper.ResultCallback.ResultCallbackDescription? {
                return HttpCallMapper.ResultCallback.ResultCallbackDescription(0, "", operation)
            }

            override fun getResult(request: Request, response: Response): String {
                return try {
                    val parameterValidationResult = RequestValidator(openAPI).validateRequest(request, operation)
                    if (!parameterValidationResult.isOk) {
                        response.status(400)
                        return parameterValidationResult.message
                    } else respond(response, operation.responses)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            }
        }
    }

    private fun respond(response: Response, responses: ApiResponses): String {
        val firstMappedStatusCode = responses.keys.first()
        val apiResponse = responses[firstMappedStatusCode]!!
        response.status(firstMappedStatusCode.toInt())
        if (apiResponse.content == null) {
            return ""
        }
        val content = apiResponse.content
        val firstContentType = getPreferredContentType(content.keys)
        response.header("Content-Type", firstContentType)
        val mediaType = content[firstContentType]!!
        if (mediaType.example != null) {
            return mediaType.example.toString()
        }
        if (mediaType.examples != null && !mediaType.examples.isEmpty()) {
            val exampleValue = mediaType.examples.values.first().value
            if (exampleValue != null) {
                return exampleValue.toString()
            }
        }
        if (mediaType.schema != null) {
            val jsonObject = ExampleResponseGenerator(openAPI).getFromSchema(mediaType.schema)
            return jsonObject.toString()
        }
        return ""
    }

    private fun getPreferredContentType(contentTypes: Set<String>): String {
        if (contentTypes.contains(PREFERRED_CONTENT_TYPE)) {
            return PREFERRED_CONTENT_TYPE
        }
        return contentTypes.first()
    }

    companion object {

        fun getSchemaByRef(ref: String, openAPI: OpenAPI): Schema<*> {
            val components = openAPI.components
            val refWithoutPrefix = ref.removePrefix("#/components/schemas/")
            return components.schemas[refWithoutPrefix]!!
        }
    }
}
