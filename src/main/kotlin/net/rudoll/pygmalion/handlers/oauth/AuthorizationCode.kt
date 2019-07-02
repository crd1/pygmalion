package net.rudoll.pygmalion.handlers.oauth

data class AuthorizationCode(val redirectUri: String, val clientId: String, val creationTime: Long, val code: String, val valid: Boolean)