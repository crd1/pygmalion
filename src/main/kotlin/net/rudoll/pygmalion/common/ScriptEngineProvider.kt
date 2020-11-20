package net.rudoll.pygmalion.common

import net.rudoll.pygmalion.model.StateHolder
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object ScriptEngineProvider {
    var engine: Engine? = null
        get() {
            if (field == null || differentEngineNameSet(field)) {
                field = createEngine()
            }
            return field
        }

    private fun differentEngineNameSet(engine: Engine?): Boolean = StateHolder.state.engineName != null && StateHolder.state.engineName != engine?.engineName

    private fun createEngine(): Engine? {
        val configuredEngineName = StateHolder.state.engineName
        return if (configuredEngineName != null) {
            createEngineByName(configuredEngineName)
        } else {
            createDefaultEngine()
        }
    }

    private fun createDefaultEngine(): Engine? {
        return createEngineByName("nashorn")
                ?: createEngineByName("graaljs")
                ?: createEngineByName("jruby")
    }

    private fun createEngineByName(engineName: String): Engine? {
        val engine = ScriptEngineManager().getEngineByName(engineName)
        if (engine != null) {
            return Engine(engine, engineName)
        }
        return null
    }

    data class Engine(val scriptEngine: ScriptEngine, val engineName: String)
}
