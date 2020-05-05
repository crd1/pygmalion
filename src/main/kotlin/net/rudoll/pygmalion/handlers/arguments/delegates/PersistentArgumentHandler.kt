package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.PersistentArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object PersistentArgumentHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--persistent: Persist data"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--persistent"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(PersistentArgument)
        input.consume(1)
    }
}
