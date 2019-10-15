package net.rudoll.pygmalion.handlers.port

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*
import net.rudoll.pygmalion.common.PortUtil

object PortHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

    override fun getDocumentation(): String {
        return "port \$port"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "port"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No port specified")
            return
        }
        val port = input.first().toIntOrNull()
        input.consume(1)
        if (port == null) {
            parsedInput.errors.add("Port is not numeric")
            return
        }
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                if (StateHolder.state.portSet) {
                    parsedInput.errors.add("Port was already set. Please use another instance of this application.")
                    return
                }
                PortUtil.setPort(port)
            }
        })
    }
}