package net.rudoll.pygmalion.cli

import net.rudoll.pygmalion.handlers.arguments.parsedarguments.VerboseArgument
import net.rudoll.pygmalion.model.Input
import net.rudoll.pygmalion.model.ParseStage
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.properties.PropertiesProvider
import net.rudoll.pygmalion.util.HandlerDiscoveryUtil.findHandlers
import java.util.*


object Cli {


    internal val handlers = findHandlers("net.rudoll.pygmalion.handlers").filter { it.getParseStage() != ParseStage.NO_PASS }.sortedBy { it.getParseStage().order }
    private val version = PropertiesProvider.getVersion()
    private val prompt = "pygmalion> "
    private val scanner = Scanner(System.`in`)

    private fun prompt() {
        System.out.print(prompt)
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
        prompt()
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

    fun log(message: String) {
        removePrompt()
        print(message)
    }

    fun print(message: String, promptBehaviour: PrintPromptBehaviour = PrintPromptBehaviour.WITH_PROMPT) {
        System.out.println(message)
        if (promptBehaviour == PrintPromptBehaviour.WITH_PROMPT) {
            prompt()
        }
    }

    fun removePrompt() {
        repeat(prompt.length) {
            System.out.print("\b")
        }
    }

    enum class PrintPromptBehaviour {
        WITH_PROMPT,
        WITHOUT_PROMPT;
    }
}