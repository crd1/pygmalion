package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.YamlArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object YamlArgumentHandler : Handler {

    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--yaml: Use YAML format"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "--yaml"
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.arguments.add(YamlArgument)
        input.consume(1)
    }
}