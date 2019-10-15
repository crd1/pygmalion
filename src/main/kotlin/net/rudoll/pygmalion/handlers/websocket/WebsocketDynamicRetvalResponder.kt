package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.common.DynamicRetValProcessor

class WebsocketDynamicRetvalResponder(private val responsePattern: String, private val dynamicRetValProcessor: DynamicRetValProcessor) : WebsocketResponder {
    override fun getResponse(message: String): String? {
        return dynamicRetValProcessor.process(responsePattern, WebsocketResponder.WebsocketRequest(message))
    }
}