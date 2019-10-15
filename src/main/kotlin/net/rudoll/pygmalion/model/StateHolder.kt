package net.rudoll.pygmalion.model

import io.swagger.v3.oas.models.OpenAPI
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.handlers.websocket.WebsocketResource

object StateHolder {
    data class State(var portSet: Boolean = false, var chaosMonkeyProbability: Int = 0, val openAPISpec: OpenAPI = OpenApiMonitor.getPrototype(), val websocketResources: MutableMap<String, WebsocketResource> = mutableMapOf())

    var state = State()

    fun reset() {
        state = State()
    }
}