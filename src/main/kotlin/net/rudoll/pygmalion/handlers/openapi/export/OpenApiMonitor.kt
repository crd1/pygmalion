package net.rudoll.pygmalion.handlers.openapi.export

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import net.rudoll.pygmalion.model.StateHolder

object OpenApiMonitor {

    fun add(method: String, route: String) {
        val paths = StateHolder.state.openAPISpec.paths
        if (paths == null || paths[route] == null) {
            StateHolder.state.openAPISpec.path(route, PathItem())
        }
        addMethod(StateHolder.state.openAPISpec.paths[route]!!, method)
    }

    private fun addMethod(pathItem: PathItem, method: String) {
        when (method.toLowerCase()) {
            "get" -> pathItem.get(getOperation())
            "post" -> pathItem.post(getOperation())
            "put" -> pathItem.put(getOperation())
            "delete" -> pathItem.delete(getOperation())
            "options" -> pathItem.options(getOperation())
        }
    }

    private fun getOperation(): Operation {
        return Operation()
    }
}