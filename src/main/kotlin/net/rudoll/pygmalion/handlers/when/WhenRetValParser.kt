package net.rudoll.pygmalion.handlers.`when`

import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.EmptyRetVal
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.FileRetVal
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.PatternRetVal
import net.rudoll.pygmalion.model.Input
import java.io.File

object WhenRetValParser {
    fun parseRetVal(input: Input): DynamicRetVal {
        val statusCode = extractStatusCode(input)
        if(!input.hasNext()) {
            return getEmptyRetValWithStatus(statusCode)
        }
        return when (input.first()) {
            "from" -> getRetValFromFile(input, statusCode)
            else -> getRetValFromPattern(input, statusCode)
        }
    }

    private fun extractStatusCode(input: Input): Int {
        return try {
            val tokens = input.getTokens()
            val statusTokenPosition = tokens.indexOf("status")
            val statusCode = tokens[statusTokenPosition + 1].toInt()
            input.consume(statusTokenPosition, 2)
            statusCode
        } catch (e: Exception) {
            200
        }
    }

    private fun getEmptyRetValWithStatus(statusCode: Int): DynamicRetVal {
        return EmptyRetVal(statusCode)
    }

    private fun getRetValFromPattern(input: Input, statusCode: Int): DynamicRetVal {
        val retVal = input.first()
        input.consume(1)
        return PatternRetVal(retVal, statusCode)
    }

    private fun getRetValFromFile(input: Input, statusCode: Int): DynamicRetVal {
        val file = File(input.second())
        input.consume(2)
        return FileRetVal(file, statusCode)
    }

}