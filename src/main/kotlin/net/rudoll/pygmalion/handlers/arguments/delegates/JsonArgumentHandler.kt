package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.JsonArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object JsonArgumentHandler : Handler {

    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--json: Use JSON format (for OpenAPI)"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--json"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(JsonArgument)
        input.consume(1)
    }
}