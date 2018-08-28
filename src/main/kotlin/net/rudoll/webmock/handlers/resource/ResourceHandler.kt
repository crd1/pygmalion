package net.rudoll.webmock.handlers.resource

import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.webmock.model.Action
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput
import net.rudoll.webmock.util.PortUtil
import spark.Spark

object ResourceHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
    override fun getDocumentation(): String {
        return "resourceTemplate \$route"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        val target = input.second()
        val portAndRoute = PortUtil.getPortAndRoute(target)
        parsedInput.actions.add(ResourceCreation(portAndRoute, parsedInput))
        input.consume(2)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first().toLowerCase() == "resourcetemplate"
    }

    class ResourceCreation(private val portAndRoute: PortUtil.PortAndRoute, private val parsedInput: ParsedInput) : Action {

        private val resourceContainer = ResourceContainer()
        override fun run(arguments: Set<ParsedArgument>) {
            if (!PortUtil.setPort(portAndRoute.port)) {
                parsedInput.errors.add("Port was already set. Route cannot be set.")
                return
            }
            val route = portAndRoute.route
            parsedInput.logs.add("Creating resource mapping for $route:${portAndRoute.port ?: "80"}")
            Spark.get(route, { _, _ -> resourceContainer.getAll() })
            Spark.get("$route/:id", { request, response -> resourceContainer.get(request.params(":id"), response) })
            Spark.post(route, { request, response -> resourceContainer.new(request.body(), response) })
            Spark.put("$route/:id", { request, response -> resourceContainer.set(request.params(":id"), request.body(),response) })
            Spark.delete("$route/:id", { request, response -> resourceContainer.delete(request.params(":id"), response) })
        }
    }
}