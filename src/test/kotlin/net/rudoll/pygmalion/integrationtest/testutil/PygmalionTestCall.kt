package net.rudoll.pygmalion.integrationtest.testutil

import org.junit.Assert
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

class PygmalionTestCall(private val target: String, private val port: Int) {
    private var expectedReturnValue: String = ""
    private var expectedStatusCode: Int = 200
    private var requestMethod: String = "GET"
    private var requestBody: String = ""


    fun thenWeExceptPygmalionToReturn(value: String, statusCode: Int = 200): PygmalionTestCall {
        this.expectedReturnValue = value
        this.expectedStatusCode = statusCode
        return this
    }

    fun and(): PygmalionTestCall {
        return this
    }

    fun withBody(body: String) : PygmalionTestCall {
        this.requestBody = body
        return this
    }

    fun withMethod(method: String): PygmalionTestCall {
        this.requestMethod = method
        return this
    }

    fun execute() {
        val targetURLString = "http://localhost:$port/$target"
        System.out.println("Calling $targetURLString with method ${this.requestMethod}")
        val testURL = URL(targetURLString)
        with(testURL.openConnection() as HttpURLConnection) {
            requestMethod = this@PygmalionTestCall.requestMethod
            if (requestMethod == "POST") {
                doOutput = true
                val wr = OutputStreamWriter(outputStream)
                wr.write(requestBody)
                wr.flush()
            }
            Assert.assertEquals(expectedStatusCode, this.responseCode)
            val response = getResponse(this.inputStream)
            Assert.assertEquals(expectedReturnValue, response)
        }
    }

    private fun getResponse(inputStream: InputStream): String {
        BufferedReader(InputStreamReader(inputStream)).use {
            return it.lines().collect(Collectors.joining("\n"))
        }
    }
}