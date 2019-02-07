package net.rudoll.pygmalion.integrationtest.testutil

class PygmalionTestSpecification(val pygmalionCommand: String) {

    internal var testCall: PygmalionTestCall? = null

    fun and(): PygmalionTestSpecification {
        return this
    }

    fun whenCallingPygmalionWith(target: String, port: Int = 80): PygmalionTestCall {
        this.testCall = PygmalionTestCall(target, port)
        return this.testCall!!
    }
}