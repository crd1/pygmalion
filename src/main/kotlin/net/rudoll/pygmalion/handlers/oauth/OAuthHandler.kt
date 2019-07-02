package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object OAuthHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No path present!")
            return
        }
        val basePath = input.first()
        OAuthRouteMapper.createOAuthRoutes(basePath, parsedInput)
        input.consume(1)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "oauth"
    }

    override fun getDocumentation(): String {
        return "oauth \$path"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}