package net.rudoll.pygmalion.handlers.websocket

import net.rudoll.pygmalion.cli.Cli
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketListener

class WebsocketResource(private val path: String, private val shouldLog: Boolean) : WebSocketListener {
    override fun onWebSocketError(cause: Throwable) {
        log("A websocket error occured: ${cause.message}")
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        log("Websocket connection to $path closed.")
    }

    override fun onWebSocketConnect(session: Session?) {
        log("Websocket connection to $path accepted.")
    }

    override fun onWebSocketText(message: String) {
        log("Websocket message to $path reveived: $message")
    }

    override fun onWebSocketBinary(payload: ByteArray?, offset: Int, len: Int) {
        log("Binary websocket message to $path received.")
    }

    private fun log(message: String?) {
        if (shouldLog) {
            Cli.log("$message")
        }
    }
}