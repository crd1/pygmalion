package net.rudoll.pygmalion.handlers.resource

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.util.HttpCallMapperUtil
import net.rudoll.pygmalion.util.PortUtil
import spark.Request
import spark.Response

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
}