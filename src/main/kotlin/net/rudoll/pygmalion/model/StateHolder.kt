package net.rudoll.pygmalion.model

import io.swagger.v3.oas.models.OpenAPI
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor

object StateHolder {
    data class State(var portSet: Boolean = false, var chaosMonkeyProbability: Int = 0, var openAPISpec: OpenAPI = OpenApiMonitor.getPrototype())

    var state = State()

    fun reset() {
        state = State()
    }
}