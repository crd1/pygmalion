package net.rudoll.webmock.handlers

import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput

interface Handler {

    fun handle(input: Input, parsedInput: ParsedInput)

    fun canHandle(input: Input): Boolean

    fun getDocumentation() : String

    fun getParseStage() : ParseStage
}