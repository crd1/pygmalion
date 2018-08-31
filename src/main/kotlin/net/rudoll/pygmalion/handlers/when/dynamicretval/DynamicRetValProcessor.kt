package net.rudoll.pygmalion.handlers.`when`.dynamicretval

import spark.Request
import java.util.regex.Pattern
import javax.script.ScriptContext
import javax.script.ScriptEngineManager

class DynamicRetValProcessor {
    private val EXPRESSION_REGEX = "\\\$\\{(.+)\\}"
    private val EXPRESSION_PATTERN = Pattern.compile(EXPRESSION_REGEX)
    private val retValCounter = RetValCounter()


    fun process(pattern: String, request: Request): String {
        return try {
            val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)
            bindings["counter"] = retValCounter.call()
            bindings["timestamp"] = System.currentTimeMillis().toString()
            bindings["body"] = request.body()

            val matcher = EXPRESSION_PATTERN.matcher(pattern)
            val processed = StringBuffer()
            while (matcher.find()) {
                val key = matcher.group(1) //binding
                val value = engine.eval(key, bindings)
                matcher.appendReplacement(processed, value.toString())
            }
            matcher.appendTail(processed)
            processed.toString()
        } catch (e: Exception) {
            pattern
        }
    }

    companion object {
        private val engine = ScriptEngineManager().getEngineByName("nashorn")
    }
}