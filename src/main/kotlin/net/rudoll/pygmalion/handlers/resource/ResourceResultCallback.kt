package net.rudoll.pygmalion.handlers.resource

import net.rudoll.pygmalion.common.HttpCallMapper

abstract class ResourceResultCallback(protected val baseResultCallbackDescription: HttpCallMapper.ResultCallback.ResultCallbackDescription) : HttpCallMapper.ResultCallback {
    override fun getResultCallbackDescription(): HttpCallMapper.ResultCallback.ResultCallbackDescription? {
        return this.baseResultCallbackDescription
    }
}