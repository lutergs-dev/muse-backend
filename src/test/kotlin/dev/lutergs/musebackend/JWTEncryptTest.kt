package dev.lutergs.musebackend

import dev.lutergs.muse.t.infra.crypto.DefaultKeyPair
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.*


class JWTEncryptTest {

  private val defaultKeyPair = DefaultKeyPair(
    "/Users/koo04034/Documents/selfDevelop/muse-backend/public_key.pem",
    "/Users/koo04034/Documents/selfDevelop/muse-backend/private_key.pem",
  )

  @Test
  fun testJWSEncrypt() {
    val current = OffsetDateTime.now()


    val alg = Jwts.KEY.RSA_OAEP_256
    val enc = Jwts.ENC.A256GCM


    val jwsResult = Jwts.builder()
      .claim("http://localhost:8081/user/id", "1")
      .issuer("muse")
      .expiration(Date.from(current.plusDays(1).toInstant()))
      .notBefore(Date.from(current.toInstant()))
      .issuedAt(Date.from(current.toInstant()))
      .encryptWith(defaultKeyPair.default.public, alg, enc)
      .compact()

    println(jwsResult)

    val decryptResult = Jwts.parser()
      .decryptWith(defaultKeyPair.default.private)
      .build()
      .parseEncryptedClaims(jwsResult)

    println(decryptResult)
  }


}