package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import java.io.File

class FileRetVal(private val file: File) : DynamicRetVal {
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun getRetVal(body: String): String {
        return dynamicRetValProcessor.process(file.readText(), body)
    }
}