package net.rudoll.pygmalion.integrationtest

import net.rudoll.pygmalion.integrationtest.testutil.PygmalionTestSpecification

object PygmalionIntegrationTestSpecificationTable {

    private var _table: MutableSet<PygmalionTestSpecification> = mutableSetOf()

    init {
        whenInputIs("when get /test then hallo").and().whenCallingPygmalionWith(target = "test").thenWeExceptPygmalionToReturn("hallo")
        whenInputIs("when get /test then status 207").and().whenCallingPygmalionWith(target = "test").thenWeExceptPygmalionToReturn("", 207)
        whenInputIs("when get /test then with status 207").and().whenCallingPygmalionWith(target = "test").thenWeExceptPygmalionToReturn("", 207)
        whenInputIs("when get /test then hallo with status 207").and().whenCallingPygmalionWith(target = "test").thenWeExceptPygmalionToReturn("hallo", 207)
        whenInputIs("when post /test then status 207").and().whenCallingPygmalionWith(target = "test").withMethod("POST").thenWeExceptPygmalionToReturn("", 207)
        whenInputIs("when post /test then \${body}").and().whenCallingPygmalionWith(target = "test").withMethod("POST").withBody("echo").thenWeExceptPygmalionToReturn("echo")
    }

    fun get(): Set<PygmalionTestSpecification> {
        return _table
    }

    fun whenInputIs(input: String): PygmalionTestSpecification {
        val pygmalionTestSpecification = PygmalionTestSpecification(input)
        _table.add(pygmalionTestSpecification)
        return pygmalionTestSpecification
    }

}