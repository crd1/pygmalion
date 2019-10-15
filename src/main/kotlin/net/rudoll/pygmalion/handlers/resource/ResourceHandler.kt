package net.rudoll.pygmalion.handlers.resource

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.PortUtil

object ResourceHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }

    override fun getDocumentation(): String {
        return "(resourceTemplate|restTemplate) \$route [from \$jsonArrayFile]"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        val target = input.second()
        val portAndRoute = PortUtil.getPortAndRoute(target)
        input.consume(2)
        var initialRepoFile: String? = null
        if (input.hasNext() && input.first() == "from") {
            input.consume(1)
            if (!input.hasNext()) {
                parsedInput.errors.add("No file specified for initial resources")
                return
            }
            initialRepoFile = input.first()
            input.consume(1)
        }
        parsedInput.actions.add(ResourceCreation(portAndRoute, parsedInput, initialRepoFile))
    }

    override fun canHandle(input: Input): Boolean {
        return input.first().toLowerCase() == "resourcetemplate" || input.first().toLowerCase() == "resttemplate"
    }
}