package net.rudoll.pygmalion.model

import io.swagger.v3.oas.models.OpenAPI

object State {
    var portSet = false
    var chaosMonkeyProbability: Int = 0
    var openAPISpec = OpenAPI()

    fun reset() {
        portSet = false
        chaosMonkeyProbability = 0
    }
}