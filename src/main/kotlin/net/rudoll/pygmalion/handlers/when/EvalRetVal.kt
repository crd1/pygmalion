package net.rudoll.pygmalion.handlers.`when`

import net.rudoll.pygmalion.common.DynamicRetValProcessor
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import spark.Request
import spark.Response
import java.io.File

class EvalRetVal(private val file: File, private val statusCode: Int) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request, response: Response): String {
        return dynamicRetValProcessor.process(file.readText(), request, evalAll = true)
    }

    override fun getStatusCode(): Int {
        return statusCode
    }
}
