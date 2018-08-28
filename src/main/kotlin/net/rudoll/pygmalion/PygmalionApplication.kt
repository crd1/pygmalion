package net.rudoll.pygmalion

import net.rudoll.pygmalion.cli.Cli.repl
import net.rudoll.pygmalion.localsocket.LocalClient
import net.rudoll.pygmalion.localsocket.LocalServer

class PygmalionApplication {

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