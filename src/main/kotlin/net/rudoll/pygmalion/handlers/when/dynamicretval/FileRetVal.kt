package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import net.rudoll.pygmalion.common.DynamicRetValProcessor
import spark.Request
import spark.Response
import java.io.File

class FileRetVal(private val file: File, private val statusCode: Int) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request, response: Response): String {
        return dynamicRetValProcessor.process(file.readText(), request)
    }

    override fun getStatusCode(): Int {
        return statusCode
    }
}
