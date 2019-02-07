package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request

interface DynamicRetVal {

    fun getRetVal(request: Request) : String

    fun getStatusCode(request: Request) : Int = 200
}