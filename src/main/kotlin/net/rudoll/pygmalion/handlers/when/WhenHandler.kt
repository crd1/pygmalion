package net.rudoll.pygmalion.handlers.`when`

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.util.HttpCallMapperUtil
import net.rudoll.pygmalion.util.PortUtil
import spark.Request
import spark.Response

object WhenHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

    override fun getDocumentation(): String {
        return "when \$method \$route [then] [from|[with ]status] \$value"
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
        val retVal: DynamicRetVal
        try {
            retVal = WhenRetValParser.parseRetVal(input)
        } catch (e: Exception) {
            parsedInput.errors.add("Return value could not be parsed. Please issue 'help' for the correct syntax.")
            return
        }
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                parsedInput.logs.add("Mapping routingContext $routingContext")
                if (!PortUtil.setPort(portAndRoute.port)) {
                    parsedInput.errors.add("Port was already set. Route cannot be set.")
                    return
                }
                val route = portAndRoute.route
                val resultCallback = object : HttpCallMapperUtil.ResultCallback {
                    override fun getResult(request: Request, response: Response): String {
                        return WhenHandler.handleCall(request, response, retVal)
                    }
                }
                HttpCallMapperUtil.map(method, route, parsedInput, resultCallback)
                if (parsedInput.arguments.contains(AllowCorsArgument)) {
                    HttpCallMapperUtil.allowPreflightRequests(route)
                }
            }
        })
    }

    private fun handleCall(request: Request, response: Response, retVal: DynamicRetVal): String {
        response.status(retVal.getStatusCode(request))
        return retVal.getRetVal(request)
    }

    private data class RoutingContext(val route: String, val method: String, val port: Int)
}