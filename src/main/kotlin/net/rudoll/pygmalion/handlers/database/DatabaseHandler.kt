package net.rudoll.pygmalion.handlers.database

import net.rudoll.pygmalion.handlers.Handler
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.model.StateHolder

object DatabaseHandler : Handler {
    override fun handle(input: Input, parsedInput: ParsedInput) {
        if (input.size() < 2) {
            parsedInput.errors.add("No database name specified")
            return
        }
        StateHolder.state.databaseName = input.second()
        input.consume(2)
    }

    override fun canHandle(input: Input): Boolean = input.first() == "database"

    override fun getDocumentation(): String = "database \$databaseName"

}
