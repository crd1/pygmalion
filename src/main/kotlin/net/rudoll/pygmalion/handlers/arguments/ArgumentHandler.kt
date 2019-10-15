package net.rudoll.pygmalion.handlers.arguments

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HandlerDiscoverer.findHandlers

object ArgumentHandler : Handler {

    private val delegateArgumentHandlers = findHandlers("net.rudoll.pygmalion.handlers.arguments.delegates")

    override fun getParseStage(): ParseStage {
        return ParseStage.SECOND_PASS
    }

    override fun getDocumentation(): String {
        val supportedArguments = StringBuilder()
        delegateArgumentHandlers.forEach { supportedArguments.append("\t${it.getDocumentation()}\n") }
        return "Supported Arguments:\n$supportedArguments"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first().startsWith("--")
    }


    override fun handle(input: Input, parsedInput: ParsedInput) {
        while (input.hasNext() && input.first().startsWith("--")) {
            val argumentHandled = tryToHandleArgumentInput(input, parsedInput)
            if(!argumentHandled) {
                parsedInput.errors.add("Could not parse argument ${input.first()}")
                return
            }
        }
    }

    private fun tryToHandleArgumentInput(input: Input, parsedInput: ParsedInput): Boolean {
        delegateArgumentHandlers.forEach {
            if (it.canHandle(input)) {
                it.handle(input, parsedInput)
                return true
            }
        }
        return false // no suitable delegate found
    }
}