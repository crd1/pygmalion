package net.rudoll.pygmalion.model

import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument

interface Action {
    fun run(arguments: Set<ParsedArgument>)
}