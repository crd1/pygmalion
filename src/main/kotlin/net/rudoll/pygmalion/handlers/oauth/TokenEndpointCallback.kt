package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.util.HttpCallMapperUtil
import spark.Request
import spark.Response
import spark.Spark.halt

class TokenEndpointCallback : HttpCallMapperUtil.ResultCallback {
    override fun getResult(request: Request, response: Response): String {
        if (!HttpCallMapperUtil.ensureAllQueryParamsPresent(request, listOf("redirect_uri", "client_id", "code", "grant_type"))) {
            return ""
        }
        val grantType = request.queryParams("grant_type")
        if (grantType != "authorization_code") {
            halt(400, "Unsupported grant_type: $grantType")
            return ""
        }
        val redirectUri = request.queryParams("redirect_uri")
        val clientId = request.queryParams("client_id")
        val authorizationCode = request.queryParams("code")
        if (!OAuthGuard.isAuthorizationCodeValid(redirectUri, clientId, authorizationCode)) {
            halt(401)
            return ""
        }
        return OAuthGuard.issueAccessToken(redirectUri, clientId)
    }
}