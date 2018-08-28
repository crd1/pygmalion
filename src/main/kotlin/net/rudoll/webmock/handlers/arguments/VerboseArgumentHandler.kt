package net.rudoll.webmock.handlers.arguments

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.handlers.arguments.parsedarguments.VerboseArgument
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput

object VerboseArgumentHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }
    override fun getDocumentation(): String {
        return "--verbose: log parsing and handling of command"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--verbose"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(VerboseArgument)
        input.consume(1)
    }
}