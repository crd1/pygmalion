package net.rudoll.pygmalion.handlers.exit

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object ExitHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
    override fun getDocumentation(): String {
        return "exit"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "exit"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        System.out.println("Bye")
        System.exit(0)
    }

}