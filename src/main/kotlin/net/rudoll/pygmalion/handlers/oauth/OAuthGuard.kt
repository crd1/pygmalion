package net.rudoll.pygmalion.handlers.oauth

import com.google.gson.Gson
import io.jsonwebtoken.Jwts
import net.rudoll.pygmalion.common.Randomizer
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.TimeUnit

object OAuthGuard {

    private val issuedAuthorizationCodes = mutableSetOf<AuthorizationCode>()
    internal val AUTHORIZATION_CODE_EXPIRATION_TIME_MS = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)
    private val ACCESS_TOKEN_EXPIRATION_TIME = TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES)
    private val privateKey = readPrivateKey()
    private val publicKey = readPublicKey()
    private val gson = Gson()

    private fun readPrivateKey(): String {
        return OAuthGuard::class.java.getResource("/private_key").readText()
    }

    private fun readPublicKey(): String {
        return OAuthGuard::class.java.getResource("/public_key").readText()
    }

    fun issueAuthorizationCode(redirectUri: String, clientId: String): AuthorizationCode {
        val authorizationCode = AuthorizationCode(redirectUri, clientId, System.currentTimeMillis(), Randomizer.getRandomString(20), true)
        storeAuthorizationCode(authorizationCode)
        return authorizationCode
    }

    private fun storeAuthorizationCode(authorizationCode: AuthorizationCode) {
        this.issuedAuthorizationCodes.add(authorizationCode)
    }

    private fun findAuthorizationCodes(redirectUri: String, clientId: String, authorizationCode: String): List<AuthorizationCode> {
        return issuedAuthorizationCodes.filter { it.valid && it.clientId == clientId && it.redirectUri == redirectUri && it.code == authorizationCode }
    }

    fun isAuthorizationCodeValid(redirectUri: String, clientId: String, authorizationCode: String): Boolean {
        return findAuthorizationCodes(redirectUri, clientId, authorizationCode).any { !it.hasExpired() }
    }

    fun issueAccessToken(authorizationCode: String, redirectUri: String, clientId: String): String {
        issuedAuthorizationCodes.removeAll(findAuthorizationCodes(redirectUri, clientId, authorizationCode)) // can only be used once
        val claims: Map<String, Any> = mapOf("exp" to createExpirationTime())
        return gson.toJson(AccessTokenResponse(JWTCreator.createJWT(claims, privateKey), "bearer", ACCESS_TOKEN_EXPIRATION_TIME.toLong() / 1000))
    }

    fun isAccessTokenValid(accessToken: String): Boolean {
        return try {
            val x509PublicKey = getX509PublicKey(publicKey)
            val jwtParser = Jwts.parser().setSigningKey(x509PublicKey)
            jwtParser.parseClaimsJws(accessToken)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createExpirationTime(): Long {
        return TimeUnit.SECONDS.convert(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS)
    }

    data class AccessTokenResponse(val access_token: String, val token_type: String, val expires_in: Long)


    private fun getX509PublicKey(publicKey: String): PublicKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val keyBytes = Base64.getDecoder().decode(publicKey)
        val x509keySpec = X509EncodedKeySpec(keyBytes)
        return keyFactory.generatePublic(x509keySpec)
    }

    class OAuthFilter(private val oauthRoutes: Set<String>) : Filter {
        override fun handle(request: Request, response: Response) {
            if (oauthRoutes.contains(request.pathInfo())) {
                return
            }
            try {
                val authHeader = request.headers("Authorization")
                val token = authHeader.replaceFirst("Bearer ", "")
                if (isAccessTokenValid(token)) {
                    return
                }
            } catch (e: Exception) {
                // simply not authenticated
            }
            Spark.halt(401)
        }
    }
}


private fun AuthorizationCode.hasExpired(): Boolean {
    return System.currentTimeMillis() - this.creationTime > OAuthGuard.AUTHORIZATION_CODE_EXPIRATION_TIME_MS
}