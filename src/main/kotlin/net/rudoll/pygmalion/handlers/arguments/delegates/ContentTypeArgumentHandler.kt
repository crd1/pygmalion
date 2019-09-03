package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ContentTypeArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object ContentTypeArgumentHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--content-type \$content-type: Set Content-Type of specified response"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--content-type"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        if (input.size() < 2) {
            parsedInput.errors.add("No content-type specified!")
            return
        }
        val contentType = input.second()
        parsedInput.arguments.add(ContentTypeArgument(contentType))
        input.consume(2)
    }
}