package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.util.HttpCallMapperUtil
import spark.Request
import spark.Response

class TokenEndpointCallback : HttpCallMapperUtil.ResultCallback {
    override fun getResult(request: Request, response: Response): String {
        //TODO
        return "token"
    }
}