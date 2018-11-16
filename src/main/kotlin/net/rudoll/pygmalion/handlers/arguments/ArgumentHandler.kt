package net.rudoll.pygmalion.handlers.arguments

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object ArgumentHandler : Handler {

    override fun getParseStage(): ParseStage {
        return ParseStage.SECOND_PASS
    }

    override fun getDocumentation(): String {
        val supportedArguments = StringBuilder()
        argumentHandlers.forEach { supportedArguments.append("\t${it.getDocumentation()}\n") }
        return "Supported Arguments:\n$supportedArguments"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first().startsWith("--")
    }

    private val argumentHandlers = listOf(VerboseArgumentHandler, LogArgumentHandler, AllowCORSArgumentHandler)

    override fun handle(input: Input, parsedInput: ParsedInput) {
        while (input.hasNext() && input.first().startsWith("--")) {
            argumentHandlers.forEach {
                if (input.hasNext() && it.canHandle(input)) {
                    it.handle(input, parsedInput)
                }
            }
        }
    }
}