package net.rudoll.pygmalion.localsocket

import net.rudoll.pygmalion.properties.PropertiesProvider
import java.io.PrintStream
import java.net.Socket
import java.util.*

object LocalClient {

    fun send(message: String) {
        val port = PropertiesProvider.getServerPort()
        val socket = Socket("localhost", port)
        val writer = PrintStream(socket.getOutputStream())
        val scanner = Scanner(socket.getInputStream())
        writer.println(message)
        scanner.nextLine() // wait for ACk
        socket.close()
    }
}