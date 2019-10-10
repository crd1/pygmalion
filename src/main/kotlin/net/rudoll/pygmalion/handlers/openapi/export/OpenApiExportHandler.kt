package net.rudoll.pygmalion.handlers.openapi.export

import io.swagger.util.Json
import io.swagger.util.Yaml
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.JsonArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*
import java.io.File

object OpenApiExportHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No file for OpenApi export specified")
            return
        }
        val file = File(input.first())
        input.consume(1)
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                file.writeText(if (arguments.contains(JsonArgument)) Json.pretty().writeValueAsString(State.openAPISpec) else Yaml.pretty().writeValueAsString(State.openAPISpec))
            }
        })
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "openapi-export"
    }

    override fun getDocumentation(): String {
        return "openapi-export \$file [--json|--yaml]"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}