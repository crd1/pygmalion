package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.NameArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object NameArgumentHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--name: Provide a name"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--name"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        if (input.size() < 2) {
            parsedInput.errors.add("No name specified!")
            return
        }
        val name = input.second()
        parsedInput.arguments.add(NameArgument(name))
        input.consume(2)
    }
}
