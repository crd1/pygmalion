package net.rudoll.pygmalion.model

class Input(rawInput: String) {

    private var tokens = listOf<String>()

    init {
        parseRawInput(rawInput)
    }

    private fun parseRawInput(rawInput: String) {
        val literals = rawInput.split("\'")
        if (literals.size % 2 == 0) {
            throw IllegalArgumentException("Input could not be parsed")
        }
        val _tokens = mutableListOf<String>()
        for ((index, literal) in literals.withIndex()) {
            if ((index + 1) % 2 == 1) {
                _tokens.addAll(literal.split(" ").filter { !it.isEmpty() })
            } else {
                _tokens.add(literal) // this was within quotation marks
            }
        }
        tokens = _tokens.toList()
    }

    fun consume(number: Int) {
        val _tokens = tokens.toMutableList()
        repeat(number, { _tokens.removeAt(0) })
        tokens = _tokens.toList()
    }

    fun isEmpty(): Boolean {
        return tokens.isEmpty()
    }

    fun getTokens(): List<String> {
        return tokens.toList()
    }

    fun hasNext(): Boolean {
        return tokens.isNotEmpty()
    }

    fun first(): String {
        return tokens[0]
    }

    fun second(): String {
        return tokens[1]
    }

    fun third(): String {
        return tokens[2]
    }

}