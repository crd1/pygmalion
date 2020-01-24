package net.rudoll.pygmalion.handlers.apply

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.*
import java.io.File

object BasedirHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No directory specified.")
            return
        }
        val dir =  File(input.first())
        input.consume(1)
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                StateHolder.state.basedir= ""
                Cli.print("", Cli.PrintPromptBehaviour.WITH_PROMPT)
                if (dir.isDirectory) {
                    StateHolder.state.basedir = dir.path;
                }
                else {
                    Cli.print("$dir is not a directory\n", Cli.PrintPromptBehaviour.WITHOUT_PROMPT)
                }
                Cli.removePrompt()
            }
        })
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "basedir"
    }

    override fun getDocumentation(): String {
        return "basedir \$directory"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}
