package net.rudoll.pygmalion.common

import io.github.classgraph.ClassGraph
import net.rudoll.pygmalion.handlers.Handler


object HandlerDiscoverer {

    fun findHandlers(packageName: String): List<Handler> {
        return ClassGraph()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan().use {
                    it.allClasses.map { classInfo -> Class.forName(classInfo.name) }
                            .filter { clazz -> Handler::class.java.isAssignableFrom(clazz) && !clazz.isInterface }
                            .mapNotNull { handlerClass -> handlerClass.kotlin.objectInstance as Handler }
                }
    }
}
