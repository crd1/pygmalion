package net.rudoll.pygmalion.handlers.oauth

import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityScheme
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor

object OAuthRouteMapper {

    private val authorizationEndpoint: String = "/authorize"
    private val tokenEndpoint: String = "/token"

    fun createOAuthRoutes(basePath: String, parsedInput: ParsedInput) {
        createAuthorizeEndpoint(basePath, parsedInput)
        createTokenEndpoint(basePath, parsedInput)
        OpenApiMonitor.addSecurityScheme("oauth", getOAuthSecurityScheme(basePath))
    }

    private fun getOAuthSecurityScheme(basePath: String): SecurityScheme {
        val scheme = SecurityScheme()
        scheme.type(SecurityScheme.Type.OAUTH2)
        scheme.description = "This API uses OAuth 2 with the authorization code grant flow."
        scheme.flows(getFlows(basePath))
        return scheme
    }

    private fun getFlows(basePath: String): OAuthFlows {
        val flows = OAuthFlows()
        flows.authorizationCode(getAuthorizationCodeFlow(basePath))
        return flows
    }

    private fun getAuthorizationCodeFlow(basePath: String): OAuthFlow {
        val authorizationCodeFlow = OAuthFlow()
        authorizationCodeFlow.authorizationUrl = "$basePath$tokenEndpoint"
        authorizationCodeFlow.tokenUrl = "$basePath$tokenEndpoint"
        return authorizationCodeFlow
    }


    private fun createTokenEndpoint(basePath: String, parsedInput: ParsedInput) {
        val endpoint = "$basePath$tokenEndpoint"
        parsedInput.logs.add("Creating endpoint $endpoint")
        HttpCallMapper.map("post", endpoint, parsedInput, TokenEndpointCallback())
    }


    private fun createAuthorizeEndpoint(basePath: String, parsedInput: ParsedInput) {
        val endpoint = "$basePath$authorizationEndpoint"
        parsedInput.logs.add("Creating endpoint $endpoint")
        HttpCallMapper.map("get", endpoint, parsedInput, AuthorizeEndpointCallback())
    }
}