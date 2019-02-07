package net.rudoll.pygmalion.integrationtest

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.integrationtest.testutil.PygmalionTestSpecification
import kotlin.test.Test

class PygmalionIntegrationTest {

    @Test
    fun executeTest() {
        System.out.println("Running integration tests.")
        val testSpecificationTable = PygmalionIntegrationTestSpecificationTable.get()
        testSpecificationTable.forEach { spec -> executePygmalionTestSpecification(spec); Thread.sleep(500) }
    }

    private fun executePygmalionTestSpecification(pygmalionTestSpecification: PygmalionTestSpecification) {
        Cli.eval(pygmalionTestSpecification.pygmalionCommand)
        pygmalionTestSpecification.testCall!!.execute()
        Cli.eval("reset")
        Cli.removePrompt()
    }
}