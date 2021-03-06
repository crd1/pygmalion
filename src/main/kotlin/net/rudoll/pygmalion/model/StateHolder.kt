package net.rudoll.pygmalion.model

import io.swagger.v3.oas.models.OpenAPI
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import net.rudoll.pygmalion.handlers.resource.ResourceContainer
import net.rudoll.pygmalion.handlers.websocket.WebsocketResource
import java.util.*

object StateHolder {
    data class State(var portSet: Boolean = false,
                     var chaosMonkeyProbability: Int = 0,
                     val openAPISpec: OpenAPI = OpenApiMonitor.getPrototype(),
                     val websocketResources: MutableMap<String, WebsocketResource> = mutableMapOf(),
                     val timerTasks: MutableSet<TimerTask> = mutableSetOf(),
                     var resourceCounter: Int = 0,
                     val resources: MutableMap<String, ResourceContainer> = mutableMapOf(),
                     var engineName: String? = null,
                     var databaseName: String = "pygmaliondb")

    var state = State()

    fun reset() {
        state.timerTasks.forEach { it.cancel() }
        state = State()
    }
}
