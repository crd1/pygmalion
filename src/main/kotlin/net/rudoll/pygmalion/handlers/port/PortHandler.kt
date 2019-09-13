package net.rudoll.pygmalion.handlers.port

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.State.portSet
import spark.Spark

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
        if (portSet) {
            parsedInput.errors.add("Port was already set. Please use another instance of this application.")
            return
        }
        Spark.port(input.second().toInt())
        portSet = true
        input.consume(2)
    }
}