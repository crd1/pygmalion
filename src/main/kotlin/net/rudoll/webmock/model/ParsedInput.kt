package net.rudoll.webmock.model

import net.rudoll.webmock.handlers.arguments.parsedarguments.ParsedArgument

class ParsedInput {

    val actions = mutableListOf<Action>()
    val errors = mutableListOf<String>()
    val arguments = mutableSetOf<ParsedArgument>()
    val logs = mutableListOf<String>()
}