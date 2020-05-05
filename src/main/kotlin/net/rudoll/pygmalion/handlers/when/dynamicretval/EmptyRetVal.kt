package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request
import spark.Response

class EmptyRetVal(private val statusCode: Int) : DynamicRetVal {
    override fun getRetVal(request: Request, response: Response): String {
        return ""
    }

    override fun getStatusCode(): Int {
        return statusCode
    }
}
