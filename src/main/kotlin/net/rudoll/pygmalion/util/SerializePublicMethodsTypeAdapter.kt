package net.rudoll.pygmalion.util

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class SerializePublicMethodsTypeAdapter(private val gsonBuilder: GsonBuilder, private val methodsToExclude: Set<String>) : TypeAdapter<Any>() {

    private val jsonParser = JsonParser()

    private val gson: Gson by lazy {
        gsonBuilder.create()
    }

    override fun write(output: JsonWriter, value: Any?) {
        if (value == null) {
            output.nullValue()
            return
        }
        output.value(serializeBasedOnPublicMethods(value))
    }

    private fun serializeBasedOnPublicMethods(value: Any): String {
        val jsonObject = JsonObject()
        for (declaredMethod in value.javaClass.declaredMethods) {
            if (!Modifier.isPublic(declaredMethod.modifiers) || declaredMethod.returnType == Void.TYPE || declaredMethod.parameterCount > 0 || methodsToExclude.contains(declaredMethod.name)) {
                continue
            }
            jsonObject.add(getPropertyName(declaredMethod), mapMethodToProperty(value, declaredMethod))
        }
        return gson.toJson(jsonObject)
    }

    private fun getPropertyName(declaredMethod: Method): String {
        val propertyName = declaredMethod.name
        return when {
            propertyName.startsWith("is") -> propertyName.replaceFirst("is", "")
            propertyName.startsWith("get") -> propertyName.replaceFirst("get", "")
            else -> propertyName
        }
    }

    private fun mapMethodToProperty(targetObject: Any, declaredMethod: Method): JsonElement {
        val value = declaredMethod.invoke(targetObject)
        return jsonParser.parse(gson.toJson(value))
    }

    override fun read(input: JsonReader): Any {
        throw IllegalStateException("This adapter cannot be used for deserializing")
    }
}
