package net.rudoll.pygmalion.util

import net.rudoll.pygmalion.handlers.arguments.parsedarguments.AllowCorsArgument
import net.rudoll.pygmalion.handlers.arguments.parsedarguments.LogArgument
import net.rudoll.pygmalion.model.ParsedInput
import spark.Request
import spark.Response
import spark.Spark

object HttpCallMapperUtil {

    fun map(method: String, route: String, parsedInput: ParsedInput, resultCallback: ResultCallback) {
        val requestHandler = { request: Request, response: Response -> handleCall(request, response, parsedInput, resultCallback) }
        when (method) {
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
        if (shouldLog) {
            System.out.println("Received call to mapped route: $request")
        }
        if (shouldAllowCORS) {
            response.header("Access-Control-Allow-Origin", "*")
        }
        return resultCallback.getResult(request, response)
    }

    interface ResultCallback {
        fun getResult(request: Request, response: Response): String
    }
}
