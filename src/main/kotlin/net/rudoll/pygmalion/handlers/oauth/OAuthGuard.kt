package net.rudoll.pygmalion.handlers.oauth

import javafx.util.Duration
import net.rudoll.pygmalion.util.RandomizerUtil

object OAuthGuard {

    private val issuedAuthorizationCodes = mutableSetOf<AuthorizationCode>()
    private val EXPIRATION_TIME_MS = Duration.minutes(10.0).toMillis()

    fun issueAuthorizationCode(redirectUri: String, clientId: String): AuthorizationCode {
        val authorizationCode = AuthorizationCode(redirectUri, clientId, System.currentTimeMillis(), RandomizerUtil.getRandomString(20), true)
        storeAuthorizationCode(authorizationCode)
        return authorizationCode
    }

    private fun storeAuthorizationCode(authorizationCode: AuthorizationCode) {
        this.issuedAuthorizationCodes.add(authorizationCode)
    }

    fun isAuthorizationCodeValid(redirectUri: String, clientId: String, authorizationCode: String): Boolean {
        return issuedAuthorizationCodes.any { it.valid && it.clientId == clientId && it.redirectUri == redirectUri && it.code == authorizationCode && !hasExpired(it) }
    }

    private fun hasExpired(authorizationCode: AuthorizationCode): Boolean {
        return System.currentTimeMillis() - authorizationCode.creationTime > EXPIRATION_TIME_MS
    }

    fun issueAccessToken(redirectUri: String, clientId: String): String {
        return "TOKEN" //TODO create JWT
    }
}