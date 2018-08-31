package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request
import java.io.File

class FileRetVal(private val file: File) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(request: Request): String {
        return dynamicRetValProcessor.process(file.readText(), request)
    }
}