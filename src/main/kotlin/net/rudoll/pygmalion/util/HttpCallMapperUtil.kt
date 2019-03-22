package net.rudoll.pygmalion.util

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.pygmalion.model.ParsedInput
import spark.Request
import spark.Response
import spark.Spark

object HttpCallMapperUtil {

    private val ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
    private val ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers"
    private val ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods"
    private val ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials"
    private val ORIGIN = "Origin"

    fun map(method: String, route: String, parsedInput: ParsedInput, resultCallback: ResultCallback) {
        val requestHandler = { request: Request, response: Response -> handleCall(request, response, parsedInput, resultCallback) }
        when (method.toLowerCase()) {
            "get" -> Spark.get(route, requestHandler)
            "post" -> Spark.post(route, requestHandler)
            "put" -> Spark.put(route, requestHandler)
            "delete" -> Spark.delete(route, requestHandler)
            "options" -> Spark.options(route, requestHandler)
            else -> parsedInput.errors.add("Unknown method.")
        }
    }

    private fun handleCall(request: Request, response: Response, parsedInput: ParsedInput, resultCallback: ResultCallback): String {
        val arguments = parsedInput.arguments
        val shouldLog = arguments.contains(LogArgument)
        val shouldAllowCORS = arguments.contains(AllowCorsArgument)
        if (shouldAllowCORS) {
            passAccessControl(request, response)
        }
        val result = resultCallback.getResult(request, response)
        if (shouldLog) {
            Cli.removePrompt()
            Cli.print("< ${DateUtil.now()} | ${request.url()} | Status ${response.status()} | Body length: ${result.length}")
        }
        return result
    }

    interface ResultCallback {
        fun getResult(request: Request, response: Response): String
    }

    fun allowPreflightRequests(route: String) {
        Spark.options(route, { request, response -> successfulPreflight(request, response) })
    }

    private fun passAccessControl(request: Request, response: Response) {
        response.header(ACCESS_CONTROL_ALLOW_ORIGIN, request.headers(ORIGIN))
        response.header(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
    }

    private fun successfulPreflight(request: Request, response: Response): String {
        passAccessControl(request, response)
        response.header(ACCESS_CONTROL_ALLOW_HEADERS, "content-type, authorization")
        response.header(ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE")
        return ""
    }
}
