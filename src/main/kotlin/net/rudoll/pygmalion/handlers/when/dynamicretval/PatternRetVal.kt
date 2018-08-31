package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request

class PatternRetVal(private val input: String) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request): String {
        return dynamicRetValProcessor.process(input, request)
    }

}