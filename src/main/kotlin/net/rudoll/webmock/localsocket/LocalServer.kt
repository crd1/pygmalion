package net.rudoll.webmock.localsocket

import net.rudoll.webmock.cli.Cli
import net.rudoll.webmock.properties.PropertiesProvider
import java.io.IOException
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

object LocalServer {
    fun startUp(): Boolean {
        try {
            val port = PropertiesProvider.getServerPort()
            val serverSocket = ServerSocket(port)
            ClientHandler(serverSocket).start()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    private class ClientHandler(val serverSocket: ServerSocket) : Thread() {
        override fun run() {

            while (true) {
                val client = serverSocket.accept()
                ClientConnection(client).start()
            }
        }
    }

    private class ClientConnection(client: Socket) : Thread() {
        val messageScanner = Scanner(client.getInputStream())
        val messageWriter = PrintStream(client.getOutputStream())

        override fun run() {
            val message = messageScanner.nextLine()
            messageWriter.println("ACK")
            Cli.print(message)
            Cli.eval(message)
        }
    }
}