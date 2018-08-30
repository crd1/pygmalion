package net.rudoll.pygmalion.handlers.apply

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import java.io.File

object ApplyHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        val file = File(input.second())
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                Cli.print("", Cli.PrintPromptBehaviour.WITH_PROMPT)
                file.readLines().filter { it.isNotEmpty() }.forEach {
                    Cli.print("$it\n", Cli.PrintPromptBehaviour.WITHOUT_PROMPT)
                    Cli.eval(it)
                }
                Cli.removePrompt()
            }
        })
        input.consume(2)
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "apply"
    }

    override fun getDocumentation(): String {
        return "apply \$file"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}