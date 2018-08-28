package net.rudoll.pygmalion.handlers.arguments

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object LogArgumentHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }
    override fun getDocumentation(): String {
        return "--log: Log calls to mapped mocks"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--log"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(LogArgument)
        input.consume(1)
    }
}