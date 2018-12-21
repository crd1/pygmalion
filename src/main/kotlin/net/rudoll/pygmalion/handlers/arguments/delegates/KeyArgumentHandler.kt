package net.rudoll.pygmalion.handlers.arguments.delegates

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.KeyArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object KeyArgumentHandler : Handler {
    private val KEY_ARGUMENT_REGEX = "--key=(.*)".toRegex()

    override fun getParseStage(): ParseStage {
        return ParseStage.NO_PASS
    }

    override fun getDocumentation(): String {
        return "--key=\$KEY: Specifies key property for restTemplates"
    }

    override fun canHandle(input: Input): Boolean {
        return input.first().matches(KEY_ARGUMENT_REGEX)
    }

    override fun handle(input: Input, parsedInput: ParsedInput) {
        val match = KEY_ARGUMENT_REGEX.find(input.first())!! // we know that this matches, we checked it before..
        val key = match.groups[1]!!.value
        parsedInput.arguments.add(KeyArgument(key))
        input.consume(1)
    }
}