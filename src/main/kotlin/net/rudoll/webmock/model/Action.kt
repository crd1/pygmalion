package net.rudoll.webmock.model

import net.rudoll.webmock.handlers.arguments.parsedarguments.ParsedArgument

interface Action {
    fun run(arguments: Set<ParsedArgument>)
}