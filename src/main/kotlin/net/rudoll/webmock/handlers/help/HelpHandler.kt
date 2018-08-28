package net.rudoll.webmock.handlers.help

import net.rudoll.webmock.cli.Cli
import net.rudoll.webmock.handlers.Handler
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput

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