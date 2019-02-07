package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request

class PatternRetVal(private val input: String, private val statusCode: Int) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request): String {
        return dynamicRetValProcessor.process(input, request)
    }

    override fun getStatusCode(request: Request): Int {
        return statusCode
    }

}