package net.rudoll.pygmalion

import net.rudoll.pygmalion.cli.Cli.repl

class PygmalionApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            repl(args.joinToString(" "))
        }
    }
}