package net.rudoll.webmock.handlers.arguments

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput

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

    private val argumentHandlers = listOf(VerboseArgumentHandler, LogArgumentHandler)

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