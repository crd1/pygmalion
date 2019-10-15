package net.rudoll.pygmalion.common

import net.rudoll.pygmalion.handlers.Handler
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.util.*

object HandlerDiscoverer {

    fun findHandlers(packageName: String): List<Handler> {
        val classLoadersList = LinkedList<ClassLoader>()
        classLoadersList.add(ClasspathHelper.contextClassLoader())
        classLoadersList.add(ClasspathHelper.staticClassLoader())
        val reflections = Reflections(ConfigurationBuilder()
                .setScanners(SubTypesScanner(false), ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(*classLoadersList.toTypedArray()))
                .filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packageName))))
        return reflections.getSubTypesOf(Handler::class.java).toList().map { handlerClass -> handlerClass.kotlin.objectInstance!! }
    }

}