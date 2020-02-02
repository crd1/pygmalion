package net.rudoll.pygmalion.handlers.openapi

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.StateHolder
import java.io.File

object OpenApiHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        var pathToOpenApiSpec = input.second()
        if (! StateHolder.state.basedir.isNullOrEmpty()){
            pathToOpenApiSpec = StateHolder.state.basedir + "/" + pathToOpenApiSpec;
        }

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
