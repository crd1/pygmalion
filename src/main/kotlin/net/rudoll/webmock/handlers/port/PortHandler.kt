package net.rudoll.webmock.handlers.port

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput
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

    var portSet = false

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