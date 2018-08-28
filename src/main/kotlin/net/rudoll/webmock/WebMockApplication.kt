package net.rudoll.webmock

import net.rudoll.webmock.cli.Cli.repl
import net.rudoll.webmock.localsocket.LocalClient
import net.rudoll.webmock.localsocket.LocalServer

class WebMockApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            if (!LocalServer.startUp()) {
                handleInLocalClientMode(args)
            } else {
                repl(args.joinToString(" "))
            }
        }

        private fun handleInLocalClientMode(args: Array<String>) {
            if (args.isEmpty()) {
                System.out.println("WebMock is already running. Cannot start in interactive mode.")
                System.exit(1)
            } else {
                LocalClient.send(args.joinToString(separator = " "))
                System.exit(0)
            }
        }

    }
}