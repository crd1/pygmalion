package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object AllowCORSArgumentHandler :Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }
    override fun getDocumentation(): String {
        return "--allow-cors: Allow CORS for this mapping."
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--allow-cors"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(AllowCorsArgument)
        input.consume(1)
    }
}