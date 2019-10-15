package net.rudoll.pygmalion.handlers.websocket

interface WebsocketResponder {
    fun getResponse(message: String): String? {
        return ""
    }
}