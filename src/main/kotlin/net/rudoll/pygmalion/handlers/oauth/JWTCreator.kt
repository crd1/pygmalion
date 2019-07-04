package net.rudoll.pygmalion.handlers.oauth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

object JWTCreator {

    fun createJWT(claims: Map<String, Any>, privateKey: String): String {
        try {
            return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.RS256, getX509PrivateKey(privateKey)).compact()
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException("Signing failed.", e)
        } catch (e: InvalidKeySpecException) {
            throw IllegalArgumentException("Signing failed.", e)
        }

    }

    private fun getX509PrivateKey(privateKey: String): Key {
        val kf = KeyFactory.getInstance("RSA")
        val keyBytes = Base64.getDecoder().decode(privateKey)
        val x509keySpec = PKCS8EncodedKeySpec(keyBytes)
        return kf.generatePrivate(x509keySpec)
    }
}