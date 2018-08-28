package net.rudoll.pygmalion.model

import net.rudoll.pygmalion.handlers.arguments.parsedarguments.ParsedArgument

class ParsedInput {

    val actions = mutableListOf<Action>()
    val errors = mutableListOf<String>()
    val arguments = mutableSetOf<ParsedArgument>()
    val logs = mutableListOf<String>()
}