package net.rudoll.pygmalion.model

object State {
    var portSet = false
    var chaosMonkeyProbability: Int = 0

    fun reset() {
        portSet = false
        chaosMonkeyProbability = 0
    }
}