package dev.lutergs.muse.infra.crypto

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwe
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.OffsetDateTime
import java.util.Date

class JwtManager(
  private val defaultKeyPair: DefaultKeyPair
) {
  private val alg = Jwts.KEY.RSA_OAEP_256
  private val enc = Jwts.ENC.A256GCM
  private val decryptor = Jwts.parser()
    .decryptWith(this.defaultKeyPair.default.private)
    .build()

  fun createToken(map: Map<String, String>, expirePeriod: Duration): String {
    val currentDateTime = OffsetDateTime.now()
    return Jwts.builder()
      .claims(map)
      .issuer("muse")
      .expiration(Date.from(currentDateTime.plusSeconds(expirePeriod.seconds).toInstant()))
      .notBefore(Date.from(currentDateTime.toInstant()))
      .issuedAt(Date.from(currentDateTime.toInstant()))
      .encryptWith(this.defaultKeyPair.default.public, this.alg, this.enc)
      .compact()
  }

  fun retrieveDataFromToken(token: String): Jwe<Claims> {
    return this.decryptor.parseEncryptedClaims(token)
  }
}