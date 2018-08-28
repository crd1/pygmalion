package net.rudoll.webmock.cli

import net.rudoll.webmock.handlers.arguments.parsedarguments.VerboseArgument
import net.rudoll.webmock.model.Input
import net.rudoll.webmock.model.ParseStage
import net.rudoll.webmock.model.ParsedInput
import net.rudoll.webmock.properties.PropertiesProvider
import net.rudoll.webmock.util.HandlerDiscoveryUtil.findHandlers
import java.util.*


object Cli {


    internal val handlers = findHandlers().filter { it.getParseStage() != ParseStage.NO_PASS }.sortedBy { it.getParseStage().order }
    private val version = PropertiesProvider.getVersion()
    private val scanner = Scanner(System.`in`)

    private fun welcome() {
        System.out.println("*********************************************")
        System.out.println("WebMock version $version, written by crd")
        System.out.println("*********************************************\n")
        prompt()
    }

    private fun prompt() {
        System.out.print("> ")
    }

    fun eval(rawInput: String) {
        val input = Input(rawInput)
        val parsedInput = ParsedInput()
        for (handler in handlers) {
            if (!handler.canHandle(input)) {
                continue
            }
            handler.handle(input, parsedInput)
            if (input.isEmpty()) {
                break
            }
        }
        if (!input.isEmpty()) {
            parsedInput.errors.add("Could not parse: ${input.getTokens()}")
        } else {
            parsedInput.actions.forEach { it.run(parsedInput.arguments.toSet()) }
        }
        parsedInput.errors.forEach { System.out.println(it) }
        if (parsedInput.arguments.contains(VerboseArgument)) {
            parsedInput.logs.forEach { System.out.println(it) }
        }
        prompt()
    }


    fun repl(initialCommand: String? = null) {
        welcome()
        initialCommand?.let {
            if (it.isNotEmpty()) {
                System.out.println(it)
                eval(it)
            }
        }
        while (scanner.hasNext()) {
            val input = read()
            eval(input)
        }
    }

    private fun read(): String {
        return scanner.nextLine()
    }

    fun print(message: String) {
        System.out.println(message)
        prompt()
    }

}