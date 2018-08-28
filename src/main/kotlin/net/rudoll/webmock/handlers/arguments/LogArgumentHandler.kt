package net.rudoll.webmock.handlers.arguments

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput

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