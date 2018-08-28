package net.rudoll.pygmalion.model

enum class ParseStage(val order: Int) {
    NO_PASS(0), FIRST_PASS(1), SECOND_PASS(2)
}