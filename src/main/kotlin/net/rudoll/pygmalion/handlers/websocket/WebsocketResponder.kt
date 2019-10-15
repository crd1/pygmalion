package net.rudoll.pygmalion.handlers.websocket

import spark.Request

interface WebsocketResponder {
    fun getResponse(message: String): String? {
        return ""
    }

    class WebsocketRequest(val message: String) : Request()
}