package dev.lutergs.muse.backend.infra.crypto

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

  fun validateTokenByTime(claims: Jwe<Claims>) {
    val currentDateTimeInstant = OffsetDateTime.now().toInstant()
    val notBefore = claims.payload.notBefore.toInstant()
    val expire = claims.payload.expiration.toInstant()

    if (notBefore.isAfter(currentDateTimeInstant)) {
      throw IllegalStateException("토큰 발급 유효기간 이전에 토큰 해석을 요청했습니다.")
    }
    if (expire.isBefore(currentDateTimeInstant)) {
      throw IllegalStateException("토큰이 만료되었습니다.")
    }
  }
}