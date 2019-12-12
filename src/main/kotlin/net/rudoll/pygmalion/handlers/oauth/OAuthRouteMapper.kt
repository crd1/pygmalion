package net.rudoll.pygmalion.handlers.oauth

import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityScheme
import net.rudoll.pygmalion.model.ParsedInput
import net.rudoll.pygmalion.common.HttpCallMapper
import net.rudoll.pygmalion.handlers.openapi.export.OpenApiMonitor
import spark.Spark.before

object OAuthRouteMapper {

    fun createOAuthRoutes(authorizationEndpoint: String, tokenEndpoint: String, parsedInput: ParsedInput) {
        createAuthorizationEndpoint(authorizationEndpoint, parsedInput)
        createTokenEndpoint(tokenEndpoint, parsedInput)
        OpenApiMonitor.addSecurityScheme("oauth", getOAuthSecurityScheme(authorizationEndpoint, tokenEndpoint))
        before(OAuthGuard.OAuthFilter(setOf(authorizationEndpoint, tokenEndpoint)))
    }

    private fun getOAuthSecurityScheme(authorizationEndpoint: String, tokenEndpoint: String): SecurityScheme {
        val scheme = SecurityScheme()
        scheme.type(SecurityScheme.Type.OAUTH2)
        scheme.description = "This API uses OAuth 2 with the authorization code grant flow."
        scheme.flows(getFlows(authorizationEndpoint, tokenEndpoint))
        return scheme
    }

    private fun getFlows(authorizationEndpoint: String, tokenEndpoint: String): OAuthFlows {
        val flows = OAuthFlows()
        flows.authorizationCode(getAuthorizationCodeFlow(authorizationEndpoint, tokenEndpoint))
        return flows
    }

    private fun getAuthorizationCodeFlow(authorizationEndpoint: String, tokenEndpoint: String): OAuthFlow {
        val authorizationCodeFlow = OAuthFlow()
        authorizationCodeFlow.authorizationUrl = authorizationEndpoint
        authorizationCodeFlow.tokenUrl = tokenEndpoint
        return authorizationCodeFlow
    }


    private fun createTokenEndpoint(tokenEndpoint: String, parsedInput: ParsedInput) {
        parsedInput.logs.add("Creating endpoint $tokenEndpoint")
        HttpCallMapper.map("post", tokenEndpoint, parsedInput, TokenEndpointCallback())
    }


    private fun createAuthorizationEndpoint(authorizationEndpoint: String, parsedInput: ParsedInput) {
        parsedInput.logs.add("Creating endpoint $authorizationEndpoint")
        HttpCallMapper.map("get", authorizationEndpoint, parsedInput, AuthorizeEndpointCallback())
    }
}