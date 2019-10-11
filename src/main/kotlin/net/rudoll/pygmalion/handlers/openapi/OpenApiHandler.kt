package net.rudoll.pygmalion.handlers.openapi

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.StateHolder
import net.rudoll.pygmalion.util.PortUtil
import java.io.File

object OpenApiHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        val pathToOpenApiSpec = input.second()
        val openApiSpecFile = File(pathToOpenApiSpec)
        input.consume(2)
        if (!openApiSpecFile.exists()) {
            parsedInput.errors.add("OpenApi specification file does not exist.")
            return
        }
        OpenApiParser.applyOpenApiSpec(openApiSpecFile, parsedInput)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "openapi"
    }

    override fun getDocumentation(): String {
        return "openapi \$path"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

}
