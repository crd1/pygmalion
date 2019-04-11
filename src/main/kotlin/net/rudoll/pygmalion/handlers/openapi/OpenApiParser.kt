package net.rudoll.pygmalion.handlers.openapi

import io.swagger.v3.parser.OpenAPIV3Parser
import net.rudoll.pygmalion.model.ParsedInput
import java.io.File

object OpenApiParser {

    fun applyOpenApiSpec(file: File, parsedInput: ParsedInput) {
        val openAPI = OpenAPIV3Parser().read(file.absolutePath)
        val openApiContext = OpenApiContext(openAPI)
        openApiContext.apply(parsedInput)
    }

}