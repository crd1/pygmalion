package net.rudoll.webmock.handlers.`when`

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.handlers.`when`.dynamicretval.DynamicRetVal
import net.rudoll.webmock.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.webmock.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.webmock.model.Action
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput
import net.rudoll.webmock.util.PortUtil
import spark.Spark

object WhenHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

    override fun getDocumentation(): String {
        return "when \$method \$route [then] [from] \$value"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "when"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        val method = input.second()
        val target = input.third()
        val portAndRoute = PortUtil.getPortAndRoute(target)
        input.consume(3)
        if (input.first() == "then") {
            input.consume(1)
        }
        val routingContext = RoutingContext(portAndRoute.route, method, portAndRoute.port ?: 80) //default to 80
        val retVal = WhenRetValParser.parseRetVal(input)
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                parsedInput.logs.add("Mapping routingContext $routingContext")
                if (!PortUtil.setPort(portAndRoute.port)) {
                    parsedInput.errors.add("Port was already set. Route cannot be set.")
                    return
                }
                val shouldLog = arguments.contains(LogArgument)
                val route = portAndRoute.route
                when (method) {
                    "get" -> Spark.get(route, { request, _ -> handleCall(request.body(), routingContext, retVal, shouldLog) })
                    "post" -> Spark.post(route, { request, _ -> handleCall(request.body(), routingContext, retVal, shouldLog) })
                    "put" -> Spark.put(route, { request, _ -> handleCall(request.body(), routingContext, retVal, shouldLog) })
                    "delete" -> Spark.delete(route, { request, _ -> handleCall(request.body(), routingContext, retVal, shouldLog) })
                    "options" -> Spark.options(route, { request, _ -> handleCall(request.body(), routingContext, retVal, shouldLog) })
                    else -> parsedInput.errors.add("Unknown method.")
                }
            }
        })
    }

    private fun handleCall(body: String, context: RoutingContext, retVal: DynamicRetVal, shouldLog: Boolean): String {
        if (shouldLog) {
            System.out.println("Received call to mapped route: $context")
        }
        return retVal.getRetVal(body)
    }

    private data class RoutingContext(val route: String, val method: String, val port: Int)
}