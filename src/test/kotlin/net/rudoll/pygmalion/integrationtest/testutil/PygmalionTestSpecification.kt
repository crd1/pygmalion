package net.rudoll.pygmalion.integrationtest.testutil

import net.rudoll.pygmalion.integrationtest.PygmalionIntegrationTest

class PygmalionTestSpecification(val pygmalionCommand: String) {

    internal var testCall: PygmalionTestCall? = null

    fun and(): PygmalionTestSpecification {
        return this
    }

    fun whenCallingPygmalionWith(target: String): PygmalionTestCall {
        this.testCall = PygmalionTestCall(target, PygmalionIntegrationTest.testPort)
        return this.testCall!!
    }
}