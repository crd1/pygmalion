package net.rudoll.pygmalion.handlers.reset

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.State
import spark.Spark

object ResetHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                Spark.stop()
                State.reset()
            }
        })
        input.consume(1)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "reset"
    }

    override fun getDocumentation(): String {
        return "reset"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}