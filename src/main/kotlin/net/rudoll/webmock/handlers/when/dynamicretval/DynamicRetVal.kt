package net.rudoll.webmock.handlers.`when`.dynamicretval

interface DynamicRetVal {

    fun getRetVal(body: String) : String
}