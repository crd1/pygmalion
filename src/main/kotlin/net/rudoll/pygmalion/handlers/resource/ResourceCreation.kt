package net.rudoll.pygmalion.handlers.resource

import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponses
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.KeyArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.common.PortManager
import net.rudoll.pygmalion.model.StateHolder
import spark.Request
import spark.Response


class ResourceCreation(private val portAndRoute: PortManager.PortAndRoute, private val parsedInput: ParsedInput, private val initialRepoFile: String?) : Action {

    private val resourceIndex = StateHolder.state.resourceCounter++


    override fun run(arguments: Set<ParsedArgument>) {
        if (!PortManager.setPort(portAndRoute.port)) {
            parsedInput.errors.add("Port was already set. Route cannot be set.")
            return
        }
        val keyProperty = readKeyArgument(arguments, parsedInput)
        val resourceContainer = ResourceContainer(keyProperty, inferResourceNameFromRoute(portAndRoute) + resourceIndex.toString())
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

    private fun inferResourceNameFromRoute(portAndRoute: PortManager.PortAndRoute): String {
        val route = portAndRoute.route
        return route.substring(route.lastIndexOf("/") + 1)
    }

    private fun readKeyArgument(arguments: Set<ParsedArgument>, parsedInput: ParsedInput): String {
        var keyProperty = ResourceContainer.DEFAULT_KEY_PROPERTY //default
        arguments.filter { it is KeyArgument }.forEach { keyProperty = (it as KeyArgument).key }
        parsedInput.logs.add("Using key property: $keyProperty")
        return keyProperty
    }

    private fun deleteCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(deleteOperationDescription()) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.delete(request.params(":id"), response)
            }
        }
    }

    private fun deleteOperationDescription(): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        return HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Deletes resource")
    }

    private fun updateCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(updateOperationDescription(resourceContainer)) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.set(request.params(":id"), request.body(), response)
            }
        }
    }

    private fun updateOperationDescription(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        val baseDescription = HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Updates the resource")
        return baseDescription.copy(operation = object : SchemaObservingOperation(resourceContainer, baseDescription) {
            override fun getRequestBody(): RequestBody {
                return getInferredRequestBody()
            }
        })
    }

    private fun createCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(createOperationDescription(resourceContainer)) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.new(request.body(), response)
            }
        }
    }

    private fun createOperationDescription(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        val baseDescription = HttpCallMapper.ResultCallback.ResultCallbackDescription(201, "Creates new resource")
        return baseDescription.copy(operation = object : SchemaObservingOperation(resourceContainer, baseDescription) {
            override fun getRequestBody(): RequestBody {
                return getInferredRequestBody()
            }

            override fun getResponses(): ApiResponses {
                return getInferredApiResponse()
            }
        })
    }

    private fun getByIdCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(getByIdOperationDescription(resourceContainer)) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.get(request.params(":id"), response);
            }
        }
    }

    private fun getByIdOperationDescription(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        val baseDescription = HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Retrieves resource by id")
        return baseDescription.copy(operation = object : SchemaObservingOperation(resourceContainer, baseDescription) {
            override fun getResponses(): ApiResponses {
                return getInferredApiResponse()
            }
        })
    }

    private fun getAllCallback(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback {
        return object : ResourceResultCallback(getAllOperationDescription(resourceContainer)) {
            override fun getResult(request: Request, response: Response): String {
                return resourceContainer.getAll()
            }
        }
    }

    private fun getAllOperationDescription(resourceContainer: ResourceContainer): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        val baseDescription = HttpCallMapper.ResultCallback.ResultCallbackDescription(200, "Retrieves all resources")
        return baseDescription.copy(operation = object : SchemaObservingOperation(resourceContainer, baseDescription) {
            override fun getResponses(): ApiResponses {
                return getInferredApiResponseArray()
            }
        })
    }
}

