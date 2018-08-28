package net.rudoll.pygmalion.handlers.help

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

object HelpHandler : Handler {
    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
    override fun handle(input: Input, parsedInput: ParsedInput) {
        val help = StringBuilder()
        input.consume(1)
        Cli.handlers.filter { if (!input.isEmpty()) it.canHandle(input) else true }.forEach { help.append("Usage: ${it.getDocumentation()}\n") }
        System.out.println(help.toString())
        if (!input.isEmpty()) {
            input.consume(1)
        }
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "help"
    }

    override fun getDocumentation(): String {
        return "help \$command"
    }
}