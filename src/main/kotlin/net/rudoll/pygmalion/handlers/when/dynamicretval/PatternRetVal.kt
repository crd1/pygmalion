package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import net.rudoll.pygmalion.common.DynamicRetValProcessor
import spark.Request
import spark.Response

class PatternRetVal(private val input: String, private val statusCode: Int) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request, response: Response): String {
        return dynamicRetValProcessor.process(input, request)
    }

    override fun getStatusCode(): Int {
        return statusCode
    }

}
