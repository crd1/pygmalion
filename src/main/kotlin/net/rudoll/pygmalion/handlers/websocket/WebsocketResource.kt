package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.common.DynamicRetValProcessor
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.*

@WebSocket
class WebsocketResource(private val path: String, private val shouldLog: Boolean) {

    private val sessions = mutableListOf<Session>()
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    @OnWebSocketError
    fun onWebSocketError(cause: Throwable) {
        log("A websocket error occured: ${cause.message}")
    }

    @OnWebSocketClose
    fun onWebSocketClose(session: Session, statusCode: Int, reason: String?) {
        log("Websocket connection to $path closed.")
        sessions.remove(session)
    }

    @OnWebSocketConnect
    fun onWebSocketConnect(session: Session) {
        log("Websocket connection to $path accepted.")
        sessions.add(session)
    }

    @OnWebSocketMessage
    fun onWebSocketText(session: Session, message: String) {
        log("Websocket message to $path reveived: $message")
    }

    fun broadcast(message: String) {
        log("Broadcasting websocket message to $path")
        sessions.filter { it.isOpen }.forEach {
            try {
                it.remote.sendString(processMessage(message))
            } catch (e: WebSocketException) {
                log("An error occured while broadcasting websocket message to $path: ${e.message}.")
            }
        }
    }

    private fun processMessage(message: String): String {
        return this.dynamicRetValProcessor.process(message, DynamicRetVal.DummyRequest)
    }

    private fun log(message: String) {
        if (shouldLog) {
            Cli.log(message)
        }
    }
}