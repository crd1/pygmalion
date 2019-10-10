package net.rudoll.pygmalion.handlers.openapi.export

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import net.rudoll.pygmalion.model.State

object OpenApiMonitor {

    fun add(method: String, route: String) {
        //TODO don't overwrite
        State.openAPISpec.path(route, getPathItem(method))
    }

    private fun getPathItem(method: String): PathItem {
        val pathItem = PathItem()
        addMethod(pathItem, method)
        return pathItem
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