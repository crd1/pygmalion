package net.rudoll.pygmalion.handlers.`when`.dynamicretval

class PatternRetVal(private val input: String) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(body: String): String {
        return dynamicRetValProcessor.process(input, body)
    }

}