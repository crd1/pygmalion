package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request

class EmptyRetVal(private val statusCode: Int) : DynamicRetVal {
    override fun getRetVal(request: Request): String {
        return ""
    }

    override fun getStatusCode(request: Request): Int {
        return statusCode
    }
}
