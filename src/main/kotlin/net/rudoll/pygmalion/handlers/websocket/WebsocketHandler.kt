package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*
import net.rudoll.pygmalion.util.PortUtil
import spark.Spark.init
import spark.Spark.webSocket

object WebsocketHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No subcommand specified.")
            return
        }
        val subcommand = input.first()
        input.consume(1)
        when (subcommand) {
            "start" -> init()
            else -> handleCommandForWebsocket(path = subcommand, input = input, parsedInput = parsedInput)
        }
    }

    private fun handleCommandForWebsocket(path: String, input: Input, parsedInput: ParsedInput) {
        //TODO
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                PortUtil.ensurePortIsSet(parsedInput)
                parsedInput.logs.add("Adding websocket resource for $path.")
                val websocketResource = WebsocketResource(path, parsedInput.arguments.contains(LogArgument))
                webSocket(path, websocketResource)
                StateHolder.state.websocketResources[path] = websocketResource
            }
        })
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "websocket"
    }

    override fun getDocumentation(): String {
        //TODO
        return "websocket TODO"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}