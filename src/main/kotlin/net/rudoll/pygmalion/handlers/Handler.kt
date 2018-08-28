package net.rudoll.pygmalion.handlers

import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput

interface Handler {

    fun handle(input: Input, parsedInput: ParsedInput)

    fun canHandle(input: Input): Boolean

    fun getDocumentation() : String

    fun getParseStage() : ParseStage
}