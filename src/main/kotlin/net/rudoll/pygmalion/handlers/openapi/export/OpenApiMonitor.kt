package net.rudoll.pygmalion.handlers.openapi.export

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import net.rudoll.pygmalion.model.StateHolder
import net.rudoll.pygmalion.util.HttpCallMapperUtil

object OpenApiMonitor {

    fun add(method: String, route: String, resultCallback: HttpCallMapperUtil.ResultCallback) {
        val resultCallbackDescription = resultCallback.getResultCallbackDescription() ?: return
        val paths = StateHolder.state.openAPISpec.paths
        if (paths == null || paths[route] == null) {
            StateHolder.state.openAPISpec.path(route, PathItem())
        }
        addMethod(StateHolder.state.openAPISpec.paths[route]!!, method, resultCallbackDescription)
    }

    private fun addMethod(pathItem: PathItem, method: String, resultCallbackDescription: HttpCallMapperUtil.ResultCallback.ResultCallbackDescription) {
        val operation = getOperation(resultCallbackDescription)
        when (method.toLowerCase()) {
            "get" -> pathItem.get(operation)
            "post" -> pathItem.post(operation)
            "put" -> pathItem.put(operation)
            "delete" -> pathItem.delete(operation)
            "options" -> pathItem.options(operation)
        }
    }

    private fun getOperation(resultCallbackDescription: HttpCallMapperUtil.ResultCallback.ResultCallbackDescription): Operation {
        if (resultCallbackDescription.operation != null) {
            return resultCallbackDescription.operation
        }
        val operation = Operation()
        operation.responses(getApiResponses(resultCallbackDescription))
        return operation
    }

    private fun getApiResponses(resultCallbackDescription: HttpCallMapperUtil.ResultCallback.ResultCallbackDescription): ApiResponses {
        return ApiResponses().addApiResponse(resultCallbackDescription.statusCode.toString(), ApiResponse().description(resultCallbackDescription.description))
    }

    fun getPrototype(): OpenAPI {
        val openApi = OpenAPI()
        openApi.info(getInfo())
        return openApi
    }

    private fun getInfo(): Info {
        val info = Info()
        info.version = "1.0"
        info.title = "Demo API"
        info.description = "Generated by Pygmalion"
        return info
    }

    fun addComponents(components: Components) {
        if (StateHolder.state.openAPISpec.components == null) {
            StateHolder.state.openAPISpec.components = Components()
        }
        components.schemas.forEach { StateHolder.state.openAPISpec.components.addSchemas(it.key, it.value) }
    }
}