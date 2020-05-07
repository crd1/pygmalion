package net.rudoll.pygmalion.handlers.static

import net.rudoll.pygmalion.common.PortManager
import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument
import net.rudoll.pygmalion.model.Action
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import spark.Spark
import java.io.File

object StaticHandler : Handler {

    override fun handle(input: Input, parsedInput: ParsedInput) {
        input.consume(1)
        if (!input.hasNext()) {
            parsedInput.errors.add("No directory specified.")
            return
        }
        val file = if (input.first() == ".") File("") else File(input.first()) //people are used to it after all..
        parsedInput.logs.add("Using static file path ${file.absolutePath}")
        input.consume(1)
        parsedInput.actions.add(object : Action {
            override fun run(arguments: Set<ParsedArgument>) {
                PortManager.ensurePortIsSet(parsedInput)
                Spark.externalStaticFileLocation(file.absolutePath)
                Spark.init()
            }
        })
    }

    override fun canHandle(input: Input): Boolean {
        return input.first() == "static"
    }

    override fun getDocumentation(): String {
        return "static \$directory"
    }

    override fun getParseStage(): ParseStage {
        return ParseStage.FIRST_PASS
    }
}
