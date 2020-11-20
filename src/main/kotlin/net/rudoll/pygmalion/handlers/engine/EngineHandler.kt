package net.rudoll.pygmalion.handlers.engine

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.common.ScriptEngineProvider
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.StateHolder

object EngineHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        if (input.size() > 1) {
            val engineName = input.second()
            val engineNameBefore = StateHolder.state.engineName
            StateHolder.state.engineName = engineName
            input.consume(1)
            if (ScriptEngineProvider.engine == null) {
                parsedInput.errors.add("Engine could not be obtained.")
                StateHolder.state.engineName = engineNameBefore
            }
        }
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                Cli.print("Current Engine: ${ScriptEngineProvider.engine?.engineName
                        ?: "none"}", Cli.PrintPromptBehaviour.WITHOUT_PROMPT)
            }
        })
        input.consume(1)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "engine"
    }

    override fun getDocumentation(): String {
        return "engine \$engineName"
    }
}
