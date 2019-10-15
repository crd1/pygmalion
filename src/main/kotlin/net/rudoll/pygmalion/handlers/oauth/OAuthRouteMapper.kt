package net.rudoll.pygmalion.handlers.oauth

import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapperUtil

object OAuthRouteMapper {
    fun createOAuthRoutes(basePath: String, parsedInput: ParsedInput) {
        createAuthorizeEndpoint(basePath, parsedInput)
        createTokenEndpoint(basePath, parsedInput)
    }

    private fun createTokenEndpoint(basePath: String, parsedInput: ParsedInput) {
        val endpoint = "$basePath/token"
        parsedInput.logs.add("Creating endpoint $endpoint")
        HttpCallMapperUtil.map("post", endpoint, parsedInput, TokenEndpointCallback())
    }

    private fun createAuthorizeEndpoint(basePath: String, parsedInput: ParsedInput) {
        val endpoint = "$basePath/authorize"
        parsedInput.logs.add("Creating endpoint $endpoint")
        HttpCallMapperUtil.map("get", endpoint, parsedInput, AuthorizeEndpointCallback())
    }
}