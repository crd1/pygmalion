package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.cli.Cli
import net.rudoll.pygmalion.common.DynamicRetValProcessor
import net.rudoll.pygmalion.handlers.`when`.dynamicretval.DynamicRetVal
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener

class WebsocketResource(private val path: String, private val shouldLog: Boolean) : WebSocketListener {

    private val sessions = mutableListOf<Session>()
    private val dynamicRetValProcessor = DynamicRetValProcessor()

    override fun onWebSocketError(cause: Throwable) {
        log("A websocket error occured: ${cause.message}")
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        log("Websocket connection to $path closed.")
    }

    override fun onWebSocketConnect(session: Session) {
        log("Websocket connection to $path accepted.")
        sessions.add(session)
    }

    override fun onWebSocketText(message: String) {
        log("Websocket message to $path reveived: $message")
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        log("Binary websocket message to $path received.")
    }

    fun broadcast(message: String) {
        sessions.filter { it.isOpen }.forEach { it.remote.sendString(processMessage(message)) }
    }

    private fun processMessage(message: String): String {
        return this.dynamicRetValProcessor.process(message, DynamicRetVal.DummyRequest)
    }

    private fun log(message: String?) {
        if (shouldLog) {
            Cli.log("$message")
        }
    }
}