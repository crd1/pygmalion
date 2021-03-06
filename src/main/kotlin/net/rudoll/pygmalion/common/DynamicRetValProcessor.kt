package net.rudoll.pygmalion.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.RetValCounter
import net.rudoll.pygmalion.handlers.websocket.WebsocketResponder
import net.rudoll.pygmalion.model.StateHolder
import spark.Request
import java.util.regex.Pattern
import javax.script.ScriptContext

class DynamicRetValProcessor {
    private val EXPRESSION_REGEX = "\\\$\\{(.+)\\}"
    private val EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEX)
    private val retValCounter = RetValCounter()
    private val gson = getGson()

    private fun getGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Request::class.java, SerializePublicMethodsTypeAdapter(gsonBuilder = gsonBuilder, methodsToExclude = setOf("session", "raw")))
        return gsonBuilder.create()
    }

    fun process(pattern: String, request: Request, evalAll: Boolean = false): String {
        val engine = ScriptEngineProvider.engine?.scriptEngine ?: return pattern
        return try {
            val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
            NashornExtension.extend(bindings)
            bindings["counter"] = retValCounter.call()
            bindings["timestamp"] = System.currentTimeMillis().toString()
            bindings["restTemplates"] = StateHolder.state.resources.mapValues { it.value.resources.values }
            if (request is WebsocketResponder.WebsocketRequest) {
                bindings["message"] = request.message
            } else if (request != DynamicRetVal.DummyRequest) {
                bindings["body"] = request.body()
                bindings["headers"] = getHeaderMap(request).toMap()
                bindings["queryParams"] = getQueryParamMap(request).toMap()
                bindings["cookies"] = request.cookies().toMap()
                bindings["uri"] = request.uri()
                bindings["request"] = gson.toJson(request)
                bindings["params"] = request.params().toMap()
            }
            if (evalAll) {
                return engine.eval(pattern, bindings)?.toString() ?: ""
            }

            val matcher = EXPRESSION_PATTERN.matcher(pattern)
            val processed = StringBuffer()
            while (matcher.find()) {
                val key = matcher.group(1) //binding
                val value = engine.eval(key, bindings)
                matcher.appendReplacement(processed, value?.toString() ?: "")
            }
            matcher.appendTail(processed)
            processed.toString()
        } catch (e: Exception) {
            pattern
        }
    }

    private fun getQueryParamMap(request: Request): Map<String, String> {
        val queryParams = request.queryParams()
        val queryParamMap = mutableMapOf<String, String>()
        queryParams.forEach { queryParamMap[it] = request.queryParams(it) }
        return queryParamMap.toMap()
    }

    private fun getHeaderMap(request: Request): Map<String, String> {
        val headers = request.headers()
        val headerMap = mutableMapOf<String, String>()
        headers.forEach { headerMap[it] = request.headers(it) }
        return headerMap.toMap()
    }
}
