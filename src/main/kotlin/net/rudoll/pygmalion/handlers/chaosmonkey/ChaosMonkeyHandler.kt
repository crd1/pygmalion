package net.rudoll.pygmalion.handlers.chaosmonkey

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*

object ChaosMonkeyHandler : Handler {

    override fun handle(input: Input, parsedInput: ParsedInput) {
        if (input.size() < 2) {
            parsedInput.errors.add("No failure probability specified")
            return
        }
        val probability = input.second().toIntOrNull()
        input.consume(2)
        if (probability == null || probability < 0 || probability > 100) {
            parsedInput.errors.add("Probability is no valid percentage")
            return
        }
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                StateHolder.state.chaosMonkeyProbability = probability
            }
        })
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "chaosmonkey"
    }

    override fun getDocumentation(): String {
        return "chaosmonkey \$percentage: fails all requests with the specified probability (in percent)"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}