package net.rudoll.pygmalion.handlers.resource

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.handlers.openapi.SchemaInference
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.handlers.openapi.export.toApiResponses

open class SchemaObservingOperation(private val resourceContainer: ResourceContainer, private val resultCallbackDescription: HttpCallMapper.ResultCallback.ResultCallbackDescription) : Operation() {

    init {
        this.responses = resultCallbackDescription.toApiResponses()
    }

    protected fun getInferredApiResponse(): ApiResponses {
        return ApiResponses().addApiResponse(resultCallbackDescription.statusCode.toString(), ApiResponse().description(resultCallbackDescription.description).content(contentWithRef()))
    }

    protected fun getInferredApiResponseArray(): ApiResponses {
        return ApiResponses().addApiResponse(resultCallbackDescription.statusCode.toString(), ApiResponse().description(resultCallbackDescription.description).content(arrayContentWithRef()))
    }

    protected fun getInferredRequestBody(): RequestBody {
        val requestBody = RequestBody()
        requestBody.content = contentWithRef()
        OpenApiMonitor.addSchema(resourceContainer.name, SchemaInference.from(resourceContainer.resources.values.toSet()))
        return requestBody
    }


    private fun contentWithRef(): Content {
        return Content().addMediaType("application/json", MediaType().schema(Schema<Any>().`$ref`(resourceContainer.name)))
    }

    private fun arrayContentWithRef(): Content {
        return Content().addMediaType("application/json", MediaType().schema(ArraySchema().type("array").items(Schema<Any>().`$ref`(resourceContainer.name))))
    }

}