package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*
import net.rudoll.pygmalion.common.PortManager
import spark.Spark.init
import spark.Spark.webSocket
import java.util.*

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
        if (!websocketResourceExists(path)) {
            parsedInput.actions.add(websocketCreationAction(path, parsedInput))
        }
        if (!input.hasNext()) {
            return
        }
        when (input.first()) {
            "message" -> handleWebsocketMessageCommand(path, input, parsedInput)
            "recurring" -> handleRecurringWebsocketMessageCommand(path, input, parsedInput)
            else -> return
        }
    }

    private fun handleRecurringWebsocketMessageCommand(path: String, input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No message specified.")
            return
        }
        val message = input.first()
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No interval specified.")
            return
        }
        val interval = input.first().toLongOrNull()
        input.consume(1)
        if (interval == null) {
            parsedInput.errors.add("Interval is not numeric.")
            return
        }
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                parsedInput.logs.add("Scheduling recurring message to $path every $interval ms.")
                val websocketResource = StateHolder.state.websocketResources[path]!!
                val timerTask = object : TimerTask() {
                    override fun run() {
                        websocketResource.broadcast(message)
                    }
                }
                Timer().scheduleAtFixedRate(timerTask, interval, interval)
                StateHolder.state.timerTasks.add(timerTask)
            }
        })
    }

    private fun handleWebsocketMessageCommand(path: String, input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No message specified")
        }
        val message = input.first()
        input.consume(1)
        parsedInput.actions.add(sendWebsocketMessageAction(path, message, parsedInput))
    }

    private fun sendWebsocketMessageAction(path: String, message: String, parsedInput: ParsedInput): Action {
        return object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                parsedInput.logs.add("Sending websocket message to $path.")
                StateHolder.state.websocketResources[path]!!.broadcast(message)
            }
        }
    }

    private fun websocketCreationAction(path: String, parsedInput: ParsedInput): Action {
        return object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                PortManager.ensurePortIsSet(parsedInput)
                parsedInput.logs.add("Adding websocket resource for $path.")
                val websocketResource = WebsocketResource(path, parsedInput.arguments.contains(LogArgument))
                webSocket(path, websocketResource)
                StateHolder.state.websocketResources[path] = websocketResource
            }
        }
    }

    private fun websocketResourceExists(path: String): Boolean {
        return StateHolder.state.websocketResources[path] != null
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "websocket"
    }

    override fun getDocumentation(): String {
        return "websocket start | \$path [message \$message | recurring \$message \$intervalInMs]"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}