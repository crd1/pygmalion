package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.util.HttpCallMapperUtil
import net.rudoll.pygmalion.util.HttpCallMapperUtil.ensureAllQueryParamsPresent
import spark.Request
import spark.Response
import spark.Spark.halt

class AuthorizeEndpointCallback : HttpCallMapperUtil.ResultCallback {
    override fun getResultCallbackDescription(): HttpCallMapperUtil.ResultCallback.ResultCallbackDescription? {
        return null
    }

    override fun getResult(request: Request, response: Response): String {
        if (!ensureAllQueryParamsPresent(request, listOf("redirect_uri", "client_id"))) {
            return ""
        }
        val redirectUri = request.queryParams("redirect_uri")
        val clientId = request.queryParams("client_id")
        val authorizationCode = OAuthGuard.issueAuthorizationCode(redirectUri, clientId)
        if (!authorizationCode.valid) {
            halt(401)
            return ""
        }
        val state = if (request.queryParams().contains("state")) {
            request.queryParams("state")
        } else null
        val redirectQueryParams = "code=${authorizationCode.code}${if (state != null) "&state=$state" else ""}"
        response.redirect("$redirectUri?$redirectQueryParams")
        return ""
    }
}