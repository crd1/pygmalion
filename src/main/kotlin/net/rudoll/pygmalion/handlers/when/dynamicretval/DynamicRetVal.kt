package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request

interface DynamicRetVal {

    fun getRetVal(request: Request = DummyRequest) : String

    fun getStatusCode() : Int = 200

    object DummyRequest : Request()
}