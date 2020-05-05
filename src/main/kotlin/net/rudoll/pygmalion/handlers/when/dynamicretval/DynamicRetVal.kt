package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request
import spark.Response

interface DynamicRetVal {

    fun getRetVal(request: Request = DummyRequest, response: Response = DummyResponse): String

    fun getStatusCode(): Int = 200

    object DummyRequest : Request()
    object DummyResponse : Response()
}
