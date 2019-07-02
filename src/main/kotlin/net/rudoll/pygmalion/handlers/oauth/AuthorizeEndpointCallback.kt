package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.util.HttpCallMapperUtil
import net.rudoll.pygmalion.util.HttpCallMapperUtil.ensureAllQueryParamsPresent
import spark.Request
import spark.Response

class AuthorizeEndpointCallback : HttpCallMapperUtil.ResultCallback {
    override fun getResult(request: Request, response: Response): String {
        if (!ensureAllQueryParamsPresent(request, listOf("redirect_uri"))) {
            return ""
        }
        //TODO maybe basic auth (e.g. if credentials present via argument)
        val redirectUri = request.queryParams("redirect_uri")
        val authorizationCode = "CODE" //TODO
        val state = if (request.queryParams().contains("state")) {
            request.queryParams("state")
        } else null
        val redirectQueryParams = "code=$authorizationCode${if (state != null) "&state=$state" else ""}"
        response.redirect("$redirectUri?$redirectQueryParams")
        return ""
    }
}