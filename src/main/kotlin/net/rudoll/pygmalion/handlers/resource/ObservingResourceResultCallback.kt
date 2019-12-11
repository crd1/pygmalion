package net.rudoll.pygmalion.handlers.resource

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.handlers.openapi.SchemaInference
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.handlers.openapi.export.toApiResponses

//FIXME remove uuid
abstract class ObservingResourceResultCallback(baseResultCallbackDescription: HttpCallMapper.ResultCallback.ResultCallbackDescription, private val resourceContainer: ResourceContainer, private val name: String) : ResourceResultCallback(baseResultCallbackDescription) {
    override fun getResultCallbackDescription(): HttpCallMapper.ResultCallback.ResultCallbackDescription {
        return this.baseResultCallbackDescription.copy(operation = getObservedOperationDescription(baseResultCallbackDescription))
    }

    private fun getObservedOperationDescription(baseResultCallbackDescription: HttpCallMapper.ResultCallback.ResultCallbackDescription): Operation {
        val baseOperation = baseResultCallbackDescription.toObservingOperation { getRequestBody() }
        baseOperation.requestBody(getRequestBody())
        return baseOperation
    }

    private fun getRequestBody(): RequestBody {
        val requestBody = RequestBody()
        requestBody.content = getContent(name)
        OpenApiMonitor.addSchema(name, SchemaInference.from(resourceContainer.resources.values.toSet()))
        return requestBody
    }

    private fun getContent(name: String): Content {
        val schema = Schema<Any>()
        schema.`$ref` = name
        val mediaType = MediaType()
        mediaType.schema = schema
        val content = Content()
        content.addMediaType("application/json", mediaType)
        return content
    }
}

private fun HttpCallMapper.ResultCallback.ResultCallbackDescription.toObservingOperation(requestBodyCallback: () -> RequestBody): Operation {
    val operation = ObservingOperation(requestBodyCallback)
    operation.responses = this.toApiResponses()
    return operation
}

class ObservingOperation(private val requestBodyCallback: () -> RequestBody) : Operation() {
    override fun getRequestBody(): RequestBody {
        return requestBodyCallback.invoke()
    }
}