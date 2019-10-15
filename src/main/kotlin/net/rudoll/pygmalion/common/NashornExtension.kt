package net.rudoll.pygmalion.common

import java.util.*
import javax.script.Bindings


object NashornExtension {

    fun extend(bindings: Bindings) {
        bindings["btoa"] = B64Encoder()
        bindings["atob"] = B64Decoder()
    }

    private class B64Encoder : java.util.function.Function<ByteArray, String> {
        override fun apply(src: ByteArray): String {
            return Base64.getEncoder().encodeToString(src)
        }
    }

    private class B64Decoder : java.util.function.Function<String, String> {
        override fun apply(src: String): String {
            return String(Base64.getDecoder().decode(src))
        }
    }
}