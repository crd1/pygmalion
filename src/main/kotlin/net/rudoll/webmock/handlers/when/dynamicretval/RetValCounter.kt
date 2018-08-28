package net.rudoll.webmock.handlers.`when`.dynamicretval

import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicLong

class RetValCounter : Callable<String> {
    private val _counter = AtomicLong(0)
    override fun call(): String {
        return (_counter.incrementAndGet()).toString()
    }
}