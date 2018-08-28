package net.rudoll.pygmalion.handlers.arguments

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.VerboseArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

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