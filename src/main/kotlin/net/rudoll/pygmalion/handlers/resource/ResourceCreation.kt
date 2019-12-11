package net.rudoll.pygmalion.handlers.resource

import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.KeyArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.common.PortManager
import spark.Request
import spark.Response


class ResourceCreation(private val portAndRoute: PortManager.PortAndRoute, private val parsedInput: ParsedInput, private val initialRepoFile: String?) : Action {

    private val resourceIndex = resourceCounter++


    override fun run(arguments: Set<ParsedArgument>) {
        if (!PortManager.setPort(portAndRoute.port)) {
            parsedInput.errors.add("Port was already set. Route cannot be set.")
            return
        }
        val keyProperty = readKeyArgument(arguments, parsedInput)
        val resourceContainer = ResourceContainer(keyProperty)
        if (initialRepoFile != null) {
            val initialized = resourceContainer.init(initialRepoFile, parsedInput)
            if (!initialized) {
                parsedInput.errors.add("ResourceTemplate could not be initialized.")
                return
            }
        }
        val route = portAndRoute.route
        parsedInput.logs.add("Creating resource mapping for $route:${portAndRoute.port ?: "80"}")
        HttpCallMapper.map("get", route, parsedInput, getAllCallback(resourceContainer))
        HttpCallMapper.map("get", "$route/:id", parsedInput, getByIdCallback(resourceContainer))
        HttpCallMapper.map("post", route, parsedInput, createCallback(resourceContainer))
        HttpCallMapper.map("put", "$route/:id", parsedInput, updateCallback(resourceContainer))
        HttpCallMapper.map("delete", "$route/:id", parsedInput, deleteCallback(resourceContainer))
        if (parsedInput.arguments.contains(AllowCorsArgument)) {
            HttpCallMapper.allowPreflightRequests(route)
            HttpCallMapper.allowPreflightRequests("$route/:id")
        }
    }

    private fun readKeyArgument(arguments: Set<ParsedArgument>, parsedInput: ParsedInput): String {
        var keyProperty = ResourceContainer.DEFAULT_KEY_PROPERTY //default
        arguments.filter { it is KeyArgument }.forEach { keyProperty = (it as KeyArgument).key }
        parsedInput.logs.add("Using key property: $keyProperty")
        return keyProperty
    }

    private fun deleteCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Deletes resource")) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.delete(request.params(":id"), response)
            }
        }
    }

    private fun updateCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ObservingResourceResultCallback(HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Updates the resource"), resourceContainer, resourceIndex.toString()) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.set(request.params(":id"), request.body(), response)
            }
        }
    }

    private fun createCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ObservingResourceResultCallback(HttpCallMapper.ResultCallback.ResultCallbackDescription(201, "Creates new resource"), resourceContainer, resourceIndex.toString()) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.new(request.body(), response)
            }
        }
    }

    private fun getByIdCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Retrieves resource by id")) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.get(request.params(":id"), response);
            }
        }
    }

    private fun getAllCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Retrieves all resources")) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.getAll()
            }
        }
    }

    companion object {
        var resourceCounter = 0
    }

}