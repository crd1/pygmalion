package net.rudoll.pygmalion.model

import io.swagger.v3.oas.models.OpenAPI

object StateHolder {
    data class State(var portSet: Boolean = false, var chaosMonkeyProbability: Int = 0, var openAPISpec: OpenAPI = OpenAPI())

    var state = State()

    fun reset() {
        state = State()
    }
}